package com.abdulhameed.foodieplan.model.factory;

import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AuthenticationRepositoryFactory {
        private static AuthenticationRepositoryFactory instance;
    
        private final FirebaseAuth firebaseAuth;
        private final FirebaseDatabase firebaseDatabase;
        private final StorageReference storageReference;
    
        private AuthenticationRepositoryFactory() {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();
        }
    
        public static AuthenticationRepositoryFactory getInstance() {
            if (instance == null) {
                instance = new AuthenticationRepositoryFactory();
            }
            return instance;
        }
    
        public AuthenticationRepository createAuthenticationRepository() {
            return new AuthenticationRepository(firebaseAuth, firebaseDatabase, storageReference);
        }
    }