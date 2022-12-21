package com.example.puzzledroidkt.querys

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FirebaseQuerys {
    var storage: FirebaseStorage? = null
    var db: Firebase? = null

    var storageRefImg1: StorageReference? = null
    var storageRefImg2: StorageReference? = null
    var storageRefImg3: StorageReference? = null
    var storageRefImg4: StorageReference? = null
    constructor(){
        storage = FirebaseStorage.getInstance();
        storageRefImg1 = storage!!.getReference().child("imagenes/adrian-enache.jpg");
        storageRefImg2 = storage!!.getReference().child("imagenes/mailchimp.jpg");
        storageRefImg3 = storage!!.getReference().child("imagenes/marcel-strauss.jpg");
        storageRefImg4 = storage!!.getReference().child("imagenes/szm.jpg");
    }

}