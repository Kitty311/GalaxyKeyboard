@file:JvmName("DemoUtils")
package com.galaxy.keyboard.model

class PhraseModel(itemId: Int = 0, input: String = "gal", predict: String = "Galaxy", freq: Int = 0, isUserText: Int = 0) {
    var mId: Int = itemId
    var mInput: String = input
    var mPredict: String = predict
    var mFreq: Int = freq
    var mIsUserText: Int = isUserText
}