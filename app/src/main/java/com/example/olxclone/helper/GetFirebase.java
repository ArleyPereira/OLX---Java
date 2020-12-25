package com.example.olxclone.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GetFirebase {

    private static DatabaseReference database;
    private static StorageReference storage;
    private static FirebaseAuth auth;

    // Retorna a instancia do Firebase Database
    public static DatabaseReference getDatabase(){
        if(database == null){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    // Retorna a instancia do Firebase Storage
    public static StorageReference getStorage(){
        if(storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }

    // Retorna a instancia do Firebase Auth
    public static FirebaseAuth getAuth(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    // Retorna se o acesso está autenticado ( true / false )
    public static boolean getAutenticado(){
        return getAuth().getCurrentUser() != null;
    }

    // Retorna o id do acesso autenticado
    public static String getIdFirebase(){
        return getAuth().getUid();
    }

    // Valida erros de cadastro e login
    public static String getMsg(String erro) {
        String msg;
        switch (erro) {
            case "The email address is already in use by another account.":
                msg = "O endereço de e-mail já está sendo usado por outra conta.";
                break;
            case "The email address is badly formatted.":
                msg = "O endereço de e-mail está formatado incorretamente.";
                break;
            case "The given password is invalid. [ Password should be at least 6 characters ]":
                msg = "A senha fornecida é inválida. A senha deve ter pelo menos 6 caracteres.";
                break;
            case "There is no user record corresponding to this identifier. The user may have been deleted.":
                msg = "Nenhuma conta encontrada com este endereço de e-mail.";
                break;
            case "The password is invalid or the user does not have a password.":
                msg = "Senha inválida.";
                break;
            default:
                msg = "Não foi possível realizar a operação, por favor tente novamente mais tarde.";
        }
        return msg;
    }

}
