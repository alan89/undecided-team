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

let kFirebaseTermsOfService = URL(string: "https://firebase.google.com/terms/")!

class ViewController: UIViewController, FUIAuthDelegate, UICollectionViewDelegate, UICollectionViewDataSource,UICollectionViewDelegateFlowLayout{
    
    @IBOutlet weak var btnAddPost: MDCFloatingButton!
    @IBOutlet weak var collectionView: UICollectionView!
    
    fileprivate(set) var auth:Auth?
    fileprivate(set) var authUI: FUIAuth? //only set internally but get externally
    fileprivate(set) var authStateListenerHandle: AuthStateDidChangeListenerHandle?
    
    var colorScheme = MDCSemanticColorScheme()
    let cardScheme = MDCCardScheme();
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
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
        collectionView.register(MDCCardCollectionCell.self, forCellWithReuseIdentifier: "Cell")
        collectionView.translatesAutoresizingMaskIntoConstraints = false
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
    
    func collectionView(_ collectionView: UICollectionView,
                        cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "Cell",
                                                      for: indexPath) as! MDCCardCollectionCell
        
        MDCCardThemer.applyScheme(cardScheme, toCardCell: cell)
        cell.isSelectable = false
        cell.cornerRadius = 8
        cell.shadowElevation(for: .selected)
        cell.shadowColor(for: .highlighted)
        cell.isAccessibilityElement = true
        cell.accessibilityLabel = "Cell"
        cell
        
        return cell
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        numberOfItemsInSection section: Int) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        sizeForItemAt indexPath: IndexPath) -> CGSize {
        let cardSize = (collectionView.bounds.size.width) - 20
        return CGSize(width: cardSize, height: cardSize)
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        insetForSectionAt section: Int) -> UIEdgeInsets {
        return UIEdgeInsets(top: 8, left: 8, bottom: 8, right: 8)
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 8
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 8
    }
    
}
