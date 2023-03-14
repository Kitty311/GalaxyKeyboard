@file:JvmName("DemoUtils")
package com.galaxy.keyboard.model

class PhraseModel(input: String = "gal", predict: String = "Galaxy", freq: Int = 0) {
    var mId: Int = 0
    var mInput: String = input
    var mPredict: String = predict
    var mFreq: Int = freq
}