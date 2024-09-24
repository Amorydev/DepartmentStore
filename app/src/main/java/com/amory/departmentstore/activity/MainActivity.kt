package com.amory.departmentstore.activity


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.amory.departmentstore.R
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.databinding.ActivityMainBinding
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.amory.departmentstore.activity.user.ChangePasswordActivity
import com.amory.departmentstore.activity.user.ChatActivity
import com.amory.departmentstore.activity.user.ChiTietDonHangActivity
import com.amory.departmentstore.activity.user.DangKiActivity
import com.amory.departmentstore.activity.user.DangNhapActivity
import com.amory.departmentstore.activity.user.GioHangActivity
import com.amory.departmentstore.activity.user.LienHeActivity
import com.amory.departmentstore.activity.user.SearchActivity
import com.amory.departmentstore.fragment.HomeFragment
import com.amory.departmentstore.fragment.PromotionFragment
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.User
import com.amory.departmentstore.retrofit.AuthInterceptor
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import io.paperdb.Paper


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Paper.init(this)

        if (Utils.kiemTraKetNoi(this)) {
            RetrofitClient.init(this)
            sharedPreferences = this.getSharedPreferences("SAVE_TOKEN", Context.MODE_PRIVATE)
            init()
            onClickMenu()
            onClickNavHeader()
            onClickSearch()
            goToCart()
            gotoChat()
            onCLickNav()
            setUpListenerBottomNavigation()

        } else {
            Toast.makeText(this, "Vui lòng kết nối internet", Toast.LENGTH_SHORT).show()
        }

    }
    private fun init(){
        if (Utils.manggiohang.getSoluong() != 0) {
            binding.badgeCart.setNumber(Utils.manggiohang.getSoluong(), true)
        } else {
            binding.badgeCart.setNumber(0)
        }
    }

    private fun setUpListenerBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.product -> {
                    val fragmentHome = HomeFragment()
                    replaceFragment(fragmentHome)
                    true
                }

                R.id.promotion ->{
                    val fragmentPromotion = PromotionFragment()
                    replaceFragment(fragmentPromotion)
                    true
                }
                else -> {
                    val fragmentHome = HomeFragment()
                    replaceFragment(fragmentHome)
                    true
                }
            }
        }
        replaceFragment(HomeFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()
    }


    private fun gotoChat() {
        binding.btnChat.setOnClickListener {
            val user = Paper.book().read<User>("user")
            if (user != null || Utils.user_current != null) {
                val intent = Intent(this, ChatActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                val intent = Intent(this, DangNhapActivity::class.java)
                startActivity(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                finish()
            }
        }
    }

    private fun onClickSearch() {
        binding.imbSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        binding.edtSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun onClickNavHeader() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val btnDangKy = headerView.findViewById<Button>(R.id.btnDangky)
        val btnDangNhap = headerView.findViewById<Button>(R.id.btnDangnhap)
        val imageProfile = headerView.findViewById<ImageView>(R.id.profile_image)
        val txtNav = headerView.findViewById<TextView>(R.id.txt_email_nav)
        val user = Paper.book().read<User>("user")
        if (user != null) {
            txtNav.text = Paper.book().read<String>("email")
            btnDangNhap.visibility = View.INVISIBLE
            btnDangKy.visibility = View.INVISIBLE
        } else {
            txtNav.text = Utils.user_current?.email
            btnDangNhap.visibility = View.INVISIBLE
            btnDangKy.visibility = View.INVISIBLE
        }

        if (user == null && Utils.user_current == null) {
            btnDangNhap.visibility = View.VISIBLE
            btnDangKy.visibility = View.VISIBLE
            imageProfile.visibility = View.INVISIBLE
            txtNav.visibility = View.INVISIBLE
        }
        btnDangKy.setOnClickListener {
            val intent = Intent(this, DangKiActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        btnDangNhap.setOnClickListener {
            val intent = Intent(this, DangNhapActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }


    private fun onCLickNav() {
        val userCurrent = Paper.book().read<User>("user")
        val menu = binding.navView.menu
        if (userCurrent == null || Utils.user_current == null) {
            menu.findItem(R.id.logout).isVisible = false
            menu.findItem(R.id.changePassword).isVisible = false
        }
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cart -> {
                    val user = Paper.book().read<User>("user")
                    if (user != null || Utils.user_current != null) {
                        val intent = Intent(this, GioHangActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, DangNhapActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                    true
                }

                R.id.details_order -> {
                    val user = Paper.book().read<User>("user")
                    if (user != null || Utils.user_current != null) {
                        val intent = Intent(this, ChiTietDonHangActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, DangNhapActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                    true
                }

                R.id.logout -> {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Đăng xuất")
                    alertDialog.setMessage("Bạn chắc chắn muốn đăng xuất")
                    alertDialog.setNegativeButton("Không") { dialog, _ ->
                        dialog.dismiss()
                    }
                    alertDialog.setPositiveButton("Có") { _, _ ->
                        Paper.book().delete("user")
                        FirebaseAuth.getInstance().signOut()
                        val editor = sharedPreferences.edit()
                        editor.remove("token")
                        editor.apply()
                        val intent = Intent(this, DangNhapActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                    alertDialog.show()
                    true
                }

                R.id.contact -> {
                    val intent = Intent(this, LienHeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    true
                }

                R.id.product -> {
                    binding.layoutDrawer.closeDrawer(binding.navView)
                    true
                }

                R.id.changePassword -> {
                    val intent = Intent(this, ChangePasswordActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    true
                }

                else -> {
                    true
                }
            }
        }
    }

    private fun onClickMenu() {
        binding.imbTrangchu.setOnClickListener {
            binding.layoutDrawer.openDrawer(binding.navView)
        }
    }


    private fun goToCart() {
        binding.imvGiohang.setOnClickListener {
            val user = Paper.book().read<User>("user")
            if (user != null || Utils.user_current != null) {
                val intent = Intent(this, GioHangActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                val intent = Intent(this, DangNhapActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun MutableList<GioHang>.getSoluong(): Int {
        var totalSoluong = 0
        for (gioHang in Utils.manggiohang) {
            totalSoluong += gioHang.soluongsanphamgiohang
        }
        return totalSoluong
    }


    override fun onResume() {
        super.onResume()
        if (Utils.manggiohang.getSoluong() != 0) {
            binding.badgeCart.setNumber(Utils.manggiohang.getSoluong(), true)
        } else {
            binding.badgeCart.setNumber(0)
        }
    }

    override fun onStart() {
        super.onStart()
        RetrofitClient.init(this)
        AuthInterceptor(this)
    }

}