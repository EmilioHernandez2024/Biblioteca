package com.example.biblioteca.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.biblioteca.R

class FragmentSetting : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvAccountName: TextView = view.findViewById(R.id.tvAccountName)
        val btnDeleteAccount: Button = view.findViewById(R.id.btnDeleteAccount)

        // Obtener el nombre de la cuenta de SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Usuario Desconocido") // "username" es la clave donde guardaste el nombre

        tvAccountName.text = username

        btnDeleteAccount.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Cuenta")
                .setMessage("¿Estás seguro de que quieres eliminar tu cuenta?")
                .setPositiveButton("Sí") { _, _ ->
                    // Aquí iría la lógica para eliminar la cuenta (si tuvieras una base de datos)
                    // Por ahora, solo mostramos un mensaje
                    android.widget.Toast.makeText(requireContext(), "Cuenta eliminada (simulado)", android.widget.Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
}