package ch.woggle.aethercatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ch.woggle.aethercatch.tinker.ItemFragment
import ch.woggle.aethercatch.tinker.dummy.DummyContent
import ch.woggle.aethercatch.ui.networks.NetworkListFragment

class MainActivity : ItemFragment.OnListFragmentInteractionListener, AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, NetworkListFragment.newInstance())
                    .commitNow()
        }
    }

    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        Log.i("TAG", "item: ${item}")
    }
}
