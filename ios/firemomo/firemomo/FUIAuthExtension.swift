//
//  FUIAuthExtension.swift
//  firemomo
//
//  Created by Vincoorbis on 7/17/18.
//  Copyright © 2018 Epam. All rights reserved.
//

import Foundation
import FirebaseUI

extension FUIAuthBaseViewController{
    open override func viewWillAppear(_ animated: Bool) {
        self.navigationItem.leftBarButtonItem = nil
    }
}
