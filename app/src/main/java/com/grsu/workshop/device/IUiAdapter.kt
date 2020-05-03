package com.grsu.workshop.device

import androidx.fragment.app.Fragment

interface IUiAdapter {

    val title: Int
    fun contentFragment(): Fragment
    fun titleFragment(): Fragment

}