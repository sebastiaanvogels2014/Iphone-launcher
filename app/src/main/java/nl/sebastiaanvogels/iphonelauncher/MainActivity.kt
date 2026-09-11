package nl.sebastiaanvogels.iphonelauncher

import android.app.*
import android.content.*
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private lateinit var grid: GridLayout
    private val apps = mutableListOf<ResolveInfo>()
    private lateinit var search: EditText

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); buildUi() }

    private fun buildUi() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(18, 34, 18, 12); setBackgroundColor(Color.rgb(18,18,22)) }
        val top = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        val title = TextView(this).apply { text = "iPhone Launcher"; textSize = 17f; setTextColor(Color.WHITE); layoutParams = LinearLayout.LayoutParams(0,60,1f) }
        val cc = TextView(this).apply { text = "⚙"; textSize = 26f; setTextColor(Color.WHITE); gravity=Gravity.CENTER; setOnClickListener { showControlCenter() } }
        top.addView(title); top.addView(cc, LinearLayout.LayoutParams(60,60)); root.addView(top)
        search = EditText(this).apply { hint = "Zoek apps"; setHintTextColor(0xffaaaaaa.toInt()); setTextColor(Color.WHITE); textSize=16f; singleLine=true; setPadding(18,0,18,0); background = rounded(0x22ffffff) }
        root.addView(search, LinearLayout.LayoutParams(-1,54).apply{setMargins(0,6,0,12)})
        grid = GridLayout(this).apply { columnCount=4; rowCount=5; alignmentMode=GridLayout.ALIGN_BOUNDS; useDefaultMargins=false }
        val scroll = ScrollView(this).apply { addView(grid); overScrollMode=View.OVER_SCROLL_NEVER }
        root.addView(scroll, LinearLayout.LayoutParams(-1,0,1f))
        val dock = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER; setPadding(8,10,8,10); background=rounded(0x44ffffff) }
        root.addView(dock, LinearLayout.LayoutParams(-1,76).apply{setMargins(4,10,4,4)})
        setContentView(root)
        search.addTextChangedListener(object: android.text.TextWatcher { override fun beforeTextChanged(s:CharSequence?,st:Int,c:Int,a:Int){}; override fun onTextChanged(s:CharSequence?,st:Int,b:Int,c:Int){ renderApps(s?.toString().orEmpty()) }; override fun afterTextChanged(e:android.text.Editable?){} })
        loadApps(); renderApps(""); renderDock(dock)
    }

    private fun loadApps(){ val i=Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER); apps.clear(); apps.addAll(packageManager.queryIntentActivities(i,0).sortedBy{it.loadLabel(packageManager).toString().lowercase(Locale.getDefault())}) }
    private fun renderApps(filter:String){ grid.removeAllViews(); val list=apps.filter{it.loadLabel(packageManager).toString().contains(filter,true)}.take(60); list.forEach{ri-> val b=appButton(ri); grid.addView(b,GridLayout.LayoutParams().apply{width=0;height=104;columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);rowSpec=GridLayout.spec(GridLayout.UNDEFINED,1f)}) } }
    private fun appButton(ri:ResolveInfo): View { val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER}; val icon=ImageView(this).apply{setImageDrawable(ri.loadIcon(packageManager)); setPadding(7,7,7,7)}; box.addView(icon,LinearLayout.LayoutParams(64,64)); box.addView(TextView(this).apply{text=ri.loadLabel(packageManager);textSize=11f;setTextColor(Color.WHITE);gravity=Gravity.CENTER;maxLines=1;ellipsize=android.text.TextUtils.TruncateAt.END},LinearLayout.LayoutParams(-1,30)); box.setOnClickListener{try{startActivity(packageManager.getLaunchIntentForPackage(ri.activityInfo.packageName))}catch(_:Exception){}}; return box }
    private fun renderDock(dock:LinearLayout){ val names=listOf("com.android.dialer","com.google.android.apps.messaging","com.sec.android.app.camera","com.google.android.apps.photos"); names.forEach{p-> val ri=apps.firstOrNull{it.activityInfo.packageName==p} ?: return@forEach; dock.addView(appButton(ri),LinearLayout.LayoutParams(0,72,1f)) } }
    private fun showControlCenter(){ val d=Dialog(this); val l=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(24,24,24,24);background=rounded(0xee222228);gravity=Gravity.CENTER}; l.addView(TextView(this).apply{text="Bedieningspaneel";textSize=24f;setTextColor(Color.WHITE);setPadding(0,0,0,20)}); listOf("Wi‑Fi","Bluetooth","Vliegtuigmodus","Donkere modus").forEach{n->l.addView(Button(this).apply{text=n;setOnClickListener{Toast.makeText(this@MainActivity,"Open de Android-instelling voor $n",Toast.LENGTH_SHORT).show()}})}; d.setContentView(l); d.window?.setBackgroundDrawableResource(android.R.color.transparent); d.show() }
    private fun rounded(color:Int)=GradientDrawable().apply{setColor(color);cornerRadius=28f}
}
