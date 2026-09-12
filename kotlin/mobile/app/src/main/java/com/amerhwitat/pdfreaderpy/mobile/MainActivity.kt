package com.amerhwitat.pdfreaderpy.mobile
import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
class MainActivity:Activity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);val r=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;setPadding(32,32,32,32)};val t=TextView(this).apply{text="PDFreaderPY — Kotlin Mobile";textSize=24f;gravity=Gravity.CENTER};val s=TextView(this).apply{text="Document ingestion\nAncient-script bridge\n128D object state: ready";textSize=16f;gravity=Gravity.CENTER;setPadding(0,24,0,24)};val b=Button(this).apply{text="Start reader";setOnClickListener{s.text="Reader: active\nProvenance-preserving objects: ready\n128D state: active"}};r.addView(t);r.addView(s);r.addView(b);setContentView(r)}}
