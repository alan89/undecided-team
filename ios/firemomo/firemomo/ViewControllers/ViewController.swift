//
//  ViewController.swift
//  firemomo
//
//  Created by Alan Escobar Martinez on 7/16/18.
//  Copyright © 2018 Epam. All rights reserved.
//

import UIKit
import Firebase
import FirebaseUI
import MaterialComponents.MaterialPalettes
import FirebaseFirestore
import KVNProgress
import SDWebImage

let kFirebaseTermsOfService = URL(string: "https://firebase.google.com/terms/")!

class ViewController: UIViewController, FUIAuthDelegate, UICollectionViewDelegate, UICollectionViewDataSource,UICollectionViewDelegateFlowLayout{
    
    @IBOutlet weak var btnAddPost: MDCFloatingButton!
    @IBOutlet weak var collectionView: UICollectionView!
    
    fileprivate(set) var auth:Auth?
    fileprivate(set) var authUI: FUIAuth? //only set internally but get externally
    fileprivate(set) var authStateListenerHandle: AuthStateDidChangeListenerHandle?
    
    var colorScheme = MDCSemanticColorScheme()
    let cardScheme = MDCCardScheme()
    var arrayMomos: [momoStruct] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.collectionView.register(UINib(nibName: "cardCell", bundle: nil), forCellWithReuseIdentifier: "momoCell")
        
        // Do any additional setup after loading the view, typically from a nib.
        self.auth = Auth.auth()
        self.authUI = FUIAuth.defaultAuthUI()
        self.authUI?.delegate = self
        self.authUI?.providers = [FUIGoogleAuth(), FUIFacebookAuth(),]
        
        self.authUI?.tosurl = kFirebaseTermsOfService
        
        self.authUI?.customStringsBundle = Bundle.main
        
        self.authStateListenerHandle = self.auth?.addStateDidChangeListener { (auth, user) in
            guard user != nil else {
                self.loginAction(sender: self)
                return
            }
        }
        
        self.navigationController?.navigationBar.isOpaque = true
        self.navigationController?.navigationBar.barStyle = UIBarStyle.black
        self.navigationController?.navigationBar.barTintColor = MDCPalette.blue.tint700
        btnAddPost.backgroundColor = MDCPalette.orange.accent700
        btnAddPost.tintColor = UIColor.white
        
        collectionView.dataSource = self
        collectionView.delegate = self
        collectionView.alwaysBounceVertical = true;
        collectionView.translatesAutoresizingMaskIntoConstraints = false
        
        getMomosCollection()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func loginAction(sender: AnyObject) {
        // Present the default login view controller provided by authUI
        let authViewController = authUI?.authViewController();
        self.present(authViewController!, animated: true, completion: nil)
        
    }
    
    func authUI(_ authUI: FUIAuth, didSignInWith user: User?, error: Error?) {
        guard let authError = error else { return }
        
        let errorCode = UInt((authError as NSError).code)
        
        switch errorCode {
        case FUIAuthErrorCode.userCancelledSignIn.rawValue:
            print("User cancelled sign-in");
            break
            
        default:
            let detailedError = (authError as NSError).userInfo[NSUnderlyingErrorKey] ?? authError
            print("Login error: \((detailedError as! NSError).localizedDescription)");
        }
    }
    
    @IBAction func logoutAction(_ sender: UIBarButtonItem) {
        try! auth!.signOut()
    }
    
    func getMomosCollection () {
        let db = Firestore.firestore()
        let settings = db.settings
        settings.areTimestampsInSnapshotsEnabled = true
        db.settings = settings
        db.collection("posts").order(by: "createdAt", descending: true).addSnapshotListener { (snapshot, error) in
            KVNProgress.show()
            self.arrayMomos = []
            guard let documents = snapshot?.documents else {
                print("Error fetching documents: \(error!)")
                return
            }
            for document in documents {
                if let commentCountValue = document.get("commentCount") as? Int,
                    let createdAtValue = document.get("createdAt") as? Timestamp,
                    let imageUrlValue = document.get("imageUrl") as? String,
                    let likesValue = document.get("likes") as? [String: Bool],
                    let likesCountValue = document.get("likesCount") as? Int,
                    let tagsValue = document.get("tags") as? [String: Bool],
                    let titleValue = document.get("title") as? String,
                    let userIdValue = document.get("userId") as? String,
                    let userNameValue = document.get("userName") as? String {
                    
                    let momo = momoStruct.init(
                        identifier: document.documentID,
                        commentCount: commentCountValue,
                        createdAt: createdAtValue,
                        imageUrl: imageUrlValue,
                        likes: likesValue,
                        likesCount: likesCountValue,
                        tags: tagsValue,
                        title: titleValue,
                        userId: userIdValue,
                        userName: userNameValue)
                    
                    self.arrayMomos.append(momo)
                }
                else {
                    print("Error con: \(document.get("title")!)")
                }
            }
            KVNProgress.dismiss()
            self.collectionView.reloadData()
        }
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "momoCell",
                                                      for: indexPath) as! cardCell
        MDCCardThemer.applyScheme(cardScheme, toCardCell: cell)
        cell.isSelectable = false
        cell.cornerRadius = 8
        cell.shadowElevation(for: .selected)
        cell.shadowColor(for: .highlighted)
        cell.isAccessibilityElement = true
        cell.accessibilityLabel = "Cell"
        
        cell.backgroundColor = MDCPalette.grey.tint100
        cell.lblTitle.text = self.arrayMomos[indexPath.row].title
        cell.lblAuthor.text = self.arrayMomos[indexPath.row].userName
        cell.lblCommentsCount.setTitle("\(self.arrayMomos[indexPath.row].commentCount)", for: .normal)
        cell.lblLikesCount.setTitle("\(self.arrayMomos[indexPath.row].likesCount)", for: .normal)
        cell.lblLikesCount.tag = indexPath.row
        cell.lblLikesCount.addTarget(self, action: #selector(buttonClicked), for: UIControlEvents.touchUpInside)
        cell.imgMomo.sd_setImage(with: URL(string: self.arrayMomos[indexPath.row].imageUrl), completed: nil)
        
        return cell
    }
    
    @objc func buttonClicked(sender: UIButton) {
        let tag = sender.tag
        var countNumber = Int(sender.titleLabel!.text!)
        print("Button Clicked \(tag)")
        
        let db = Firestore.firestore()
        let userID = Auth.auth().currentUser!.uid
        print("User ID : \(userID)");
        print("Like exists?: \(arrayMomos[tag].tags[userID] != nil)")
        // User already liked the post
        if arrayMomos[tag].likes[userID] != nil {
            countNumber = countNumber! - 1
            db.collection("posts").document(arrayMomos[tag].identifier).updateData([
                "likes.\(userID)": FieldValue.delete(),
                "likesCount": countNumber!
            ]) { err in
                if let err = err {
                    print("Error updating document: \(err)")
                } else {
                    print("Document successfully updated")
                    self.arrayMomos[tag].likes.removeValue(forKey: userID)
                    sender.setTitle("\(countNumber!)", for: .normal)
                }
            }
        }
        // User doesn't exist in likes list
        else {
            countNumber = countNumber! + 1
            db.collection("posts").document(arrayMomos[tag].identifier).setData([
                    "likes": [userID: true],
                    "likesCount": countNumber!
            ] , merge: true ) { err in
                if let err = err {
                    print("Error updating document: \(err)")
                } else {
                    print("Document successfully updated")
                    self.arrayMomos[tag].likes[userID] = true
                    sender.setTitle("\(countNumber!)", for: .normal)
                }
            }
        }
    }
    
    @IBAction func clickOnNewPost(_ sender: MDCFloatingButton) {
        let destinationViewController = storyboard?.instantiateViewController(withIdentifier: "newPost") as! uploadMomoVC
        navigationController?.pushViewController(destinationViewController, animated: true)
    }
    
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        numberOfItemsInSection section: Int) -> Int {
        return self.arrayMomos.count
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        sizeForItemAt indexPath: IndexPath) -> CGSize {
        let cardSize = (collectionView.bounds.size.width) - 20
        return CGSize(width: cardSize, height: cardSize + (cardSize/3))
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        insetForSectionAt section: Int) -> UIEdgeInsets {
        return UIEdgeInsets(top: 8, left: 8, bottom: 8, right: 8)
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 10
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 10
    }
    
}
