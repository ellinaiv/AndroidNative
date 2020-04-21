package com.example.team11

interface OperationCallback<T> {
    fun onSuccess(data:Array<T>?)
    fun onError(error:String?)
}