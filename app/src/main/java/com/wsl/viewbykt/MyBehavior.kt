package com.wsl.viewbykt

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView

class MyBehavior : CoordinatorLayout.Behavior<View> {

    var last:Float = 0f
    var height = 0

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View
    ): Boolean {
        return dependency is RecyclerView
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View
    ): Boolean {
        if (last == 0f){
            last = dependency.y
            height = parent.height - child.top
            Log.i("tag","${parent.height} -" + height)
        }
        child.translationY = (last-dependency.y)/last * height
        child.alpha = (dependency.y)/last
//        child.scaleY = child.scaleX
        return true
    }
}