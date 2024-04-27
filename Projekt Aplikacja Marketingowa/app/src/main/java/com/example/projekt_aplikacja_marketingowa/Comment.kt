package com.example.projekt_aplikacja_marketingowa

data class Comment(val commentId : String, val commentUserId : String, val commentPostId : String, val commentText : String, val commentDate : com.google.firebase.Timestamp)