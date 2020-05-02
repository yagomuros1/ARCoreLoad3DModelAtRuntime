package com.yago.loadmodelyago

import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ASSET_3D =
            "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf"
    }

    private var arFragment: ArFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment?

        arFragment?.let { arFragment ->
            arFragment.setOnTapArPlaneListener { hitResult: HitResult, _: Plane?, _: MotionEvent? ->
                placeModel(hitResult.createAnchor())
            }
        }
    }

    private fun placeModel(anchor: Anchor) {
        ModelRenderable.builder().setSource(
            this,
            RenderableSource.builder()
                .setSource(this, Uri.parse(ASSET_3D), RenderableSource.SourceType.GLTF2)
                .setScale(0.75f).setRecenterMode(RenderableSource.RecenterMode.ROOT).build()
        ).setRegistryId(ASSET_3D).build().thenAccept { modelRender: ModelRenderable ->
            addNodeToScene(modelRender, anchor)
        }.exceptionally { throwable: Throwable ->
            val builder =
                AlertDialog.Builder(this)
            builder.setMessage(throwable.message).show()
            null
        }
    }

    private fun addNodeToScene(modelRender: ModelRenderable, anchor: Anchor) {
        val anchorNode = AnchorNode(anchor)
        anchorNode.renderable = modelRender
        arFragment?.arSceneView?.scene?.addChild(anchorNode)
    }
}
