//
//  FUIAuthExtension.swift
//  firemomo
//
//  Created by Vincoorbis on 7/17/18.
//  Copyright © 2018 Epam. All rights reserved.
//

import Foundation
import FirebaseUI
import MaterialComponents.MaterialPalettes

extension FUIAuthBaseViewController{
    open override func viewWillAppear(_ animated: Bool) {
        self.navigationItem.leftBarButtonItem = nil
        self.navigationController?.navigationBar.barStyle = UIBarStyle.black
        self.navigationController?.navigationBar.isOpaque = true
        self.navigationController?.navigationBar.isTranslucent = false
        self.navigationController?.navigationBar.barTintColor = MDCPalette.blue.tint700
        self.navigationController?.navigationBar.titleTextAttributes = [NSAttributedStringKey(rawValue: NSAttributedStringKey.foregroundColor.rawValue): UIColor.white]
    }
}
