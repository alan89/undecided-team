//
//  uploadMomoVC.swift
//  firemomo
//
//  Created by Alan Escobar Martinez on 7/19/18.
//  Copyright © 2018 Epam. All rights reserved.
//

import UIKit
import MaterialComponents

class uploadMomoVC: UIViewController, UITextFieldDelegate {
    @IBOutlet weak var txtFldTitle: MDCTextField!
    @IBOutlet weak var txtFldUpText: MDCTextField!
    @IBOutlet weak var txtFldDownText: MDCTextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationController?.navigationBar.isOpaque = true
        self.navigationController?.navigationBar.barStyle = UIBarStyle.black
        self.navigationController?.navigationBar.barTintColor = MDCPalette.blue.tint700
        //btnAddPost.backgroundColor = MDCPalette.orange.accent700
        //btnAddPost.tintColor = UIColor.white
        
        txtFldTitle.textColor = UIColor(white: 0, alpha: MDCTypography.headlineFontOpacity())
        txtFldTitle.delegate = self
        txtFldTitle.placeholder = "TITLE"
        txtFldTitle.font = UIFont.preferredFont(forTextStyle: .headline)
        txtFldTitle.autocapitalizationType = .allCharacters
        
        txtFldUpText.textColor = UIColor(white: 1, alpha: MDCTypography.body1FontOpacity())
        txtFldUpText.underline?.isHidden = true
        txtFldUpText.autocapitalizationType = .allCharacters
        txtFldUpText.font = UIFont.preferredFont(forTextStyle: .title1)
        txtFldUpText.layer.shadowColor = UIColor.black.cgColor
        txtFldUpText.layer.shadowRadius = 2.0
        txtFldUpText.layer.shadowOpacity = 1.0
        txtFldUpText.layer.shadowOffset = CGSize(width: 0, height: 0)
        txtFldUpText.layer.masksToBounds = false
        
        txtFldDownText.textColor = UIColor(white: 1, alpha: MDCTypography.body1FontOpacity())
        txtFldDownText.underline?.isHidden = true
        txtFldDownText.autocapitalizationType = .allCharacters
        txtFldDownText.font = UIFont.preferredFont(forTextStyle: .title1)
        txtFldDownText.layer.shadowColor = UIColor.black.cgColor
        txtFldDownText.layer.shadowRadius = 2.0
        txtFldDownText.layer.shadowOpacity = 1.0
        txtFldDownText.layer.shadowOffset = CGSize(width: 0, height: 0)
        txtFldDownText.layer.masksToBounds = false
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
