package com.example.loginsignupauth

import android.os.Bundle
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignupauth.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.app.Dialog
import android.view.Window
import com.example.loginsignupauth.R

class UserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri: Uri
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        storageReference = FirebaseStorage.getInstance().getReference("ProfilePictures/${auth.currentUser?.uid}")

        binding.saveBtn.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        val firstName = binding.firstName.text.toString()
        val lastName = binding.lastName.text.toString()
        val bio = binding.bio.text.toString()
        val uid = auth.currentUser?.uid

        if (uid != null) {
            val profileData = ProfileDataClass(firstName, lastName, bio)
            databaseReference.child(uid).setValue(profileData).addOnSuccessListener {
                uploadProfilePicture()
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to Update Profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadProfilePicture() {
        imageUri = Uri.parse("android.resource://$packageName/${R.drawable.profile_picture}")
        storageReference.putFile(imageUri).addOnSuccessListener {
            Toast.makeText(this, "Profile Picture Uploaded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to Upload Profile Picture", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProgressBar() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun hideProgressBar() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}
