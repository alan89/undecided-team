//
//  momoStructure.swift
//  firemomo
//
//  Created by Alan Escobar Martinez on 7/19/18.
//  Copyright © 2018 Epam. All rights reserved.
//

import Foundation
import FirebaseFirestore

struct momoStruct{
    let identifier: String
    let commentCount: Int
    let createdAt: Timestamp
    let imageUrl: String
    var likes: [String: Bool]
    let likesCount: Int
    var tags: [String: Bool]
    let title: String
    let userId: String
    let userName: String
}
