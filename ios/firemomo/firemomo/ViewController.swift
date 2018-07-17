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

let kFirebaseTermsOfService = URL(string: "https://firebase.google.com/terms/")!

class ViewController: UIViewController, FUIAuthDelegate{
    @IBOutlet weak var btnLogout: UIButton!
    
    fileprivate(set) var auth:Auth?
    fileprivate(set) var authUI: FUIAuth? //only set internally but get externally
    fileprivate(set) var authStateListenerHandle: AuthStateDidChangeListenerHandle?
    
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
    
    @IBAction func actnLogout(_ sender: UIButton) {
        try! auth!.signOut()
    }
    
}
