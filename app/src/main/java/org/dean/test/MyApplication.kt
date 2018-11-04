package org.dean.test

import android.app.Application
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

class MyApplication: Application()

@GlideModule
class TestGlideModule : AppGlideModule()