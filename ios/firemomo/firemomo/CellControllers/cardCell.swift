//
//  cardCell.swift
//  firemomo
//
//  Created by Alan Escobar Martinez on 7/18/18.
//  Copyright © 2018 Epam. All rights reserved.
//

import UIKit
import MaterialComponents

class cardCell: MDCCardCollectionCell {
    @IBOutlet weak var containerView: UIView!
    @IBOutlet weak var lblTitle: UILabel!
    @IBOutlet weak var lblCreationDate: UILabel!
    @IBOutlet weak var imgMomo: UIImageView!
    @IBOutlet weak var lblAuthor: UILabel!
    @IBOutlet weak var lblLikesCount: UIButton!
    @IBOutlet weak var lblCommentsCount: UIButton!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.contentView.autoresizingMask = .flexibleHeight
        // Initialization code
    }
    
}
