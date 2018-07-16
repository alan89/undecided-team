//
//  ViewController.swift
//  firemomo
//
//  Created by Alan Escobar Martinez on 7/16/18.
//  Copyright © 2018 Epam. All rights reserved.
//

import UIKit
import Firebase
import GoogleSignIn
import FBSDKLoginKit

class ViewController: UIViewController, GIDSignInUIDelegate, FBSDKLoginButtonDelegate {
    func loginButton(_ loginButton: FBSDKLoginButton!, didCompleteWith result: FBSDKLoginManagerLoginResult!, error: Error!) {
        if let error = error {
            print(error.localizedDescription)
            return
        }
        else {
            print("User id logged in: \(result.token.userID!)")
            let credential = FacebookAuthProvider.credential(withAccessToken: FBSDKAccessToken.current().tokenString)
            Auth.auth().signInAndRetrieveData(with: credential) { (authResult, error) in
                if let error = error {
                    print("Facebook error: \(error)")
                    return
                }
                print("Facebook Logged in: \(authResult!)")
            }
        }
    }
    
    func loginButtonDidLogOut(_ loginButton: FBSDKLoginButton!) {
        print("Logout")
    }
    
    @IBOutlet weak var btnGoogleSignIn: GIDSignInButton!
    @IBOutlet weak var btnFacebookLogin: FBSDKLoginButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        GIDSignIn.sharedInstance().uiDelegate = self
        btnFacebookLogin.delegate = self
        btnFacebookLogin.readPermissions = ["email", "public_profile"]
        btnGoogleSignIn.style = .wide
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }


}

