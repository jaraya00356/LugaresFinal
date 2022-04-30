package com.lugares.ui.lugar

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lugares.R
import com.lugares.databinding.FragmentUpdateLugarBinding
import com.lugares.model.Lugar
import com.lugares.viewmodel.LugarViewModel

class UpdateLugarFragment : Fragment() {

    private lateinit var lugarViewModel: LugarViewModel
    private val args by navArgs<UpdateLugarFragmentArgs>()


    private var _binding: FragmentUpdateLugarBinding? = null
    private val binding get()= _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel = ViewModelProvider (this )[LugarViewModel::class.java]

        _binding = FragmentUpdateLugarBinding.inflate(inflater,container,false)

        binding.etNombre.setText(args.lugar.nombre)
        binding.etCorreo.setText(args.lugar.correo)
        binding.etTelefono.setText(args.lugar.telefono)
        binding.etWeb.setText(args.lugar.web)
        binding.tvLatitud.setText(""+args.lugar.latitud)
        binding.tvLongitud.setText(""+args.lugar.longitud)
        binding.tvAltura.setText(""+args.lugar.altura)


        binding.btActualizar.setOnClickListener {updateLugar() }
        binding.btEmail.setOnClickListener {enviarCorreo() }
        binding.btPhone.setOnClickListener {hacerLlamada() }
        binding.btWhatsapp.setOnClickListener {enviarWhatsapp() }
        binding.btWeb.setOnClickListener {verWeb() }
        binding.btLocation.setOnClickListener {verMapa() }


        setHasOptionsMenu(true)

        return  binding.root
    }

    private fun enviarCorreo() {
        val destinatario = binding.etCorreo.text.toString()
        if(destinatario.isNotEmpty()){
            val intent = Intent(Intent.ACTION_SEND)
            intent.type="message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))

            intent.putExtra(Intent.EXTRA_SUBJECT,
            getString(R.string.msg_saludos)+""+binding.etNombre.text)

            intent.putExtra(Intent.EXTRA_TEXT,
            getString(R.string.msg_mensaje_correo))

            startActivity(intent)


        }else{
            Toast.makeText(requireContext(),
            getString(R.string.msg_datos),Toast.LENGTH_LONG).show()
        }
    }

    private fun hacerLlamada() {
        val telefono = binding.etTelefono.text.toString()
        if(telefono.isNotEmpty()){
            val intent = Intent(Intent.ACTION_CALL)
            intent.data= Uri.parse("tel:$telefono")
            if(requireActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) !=PackageManager.PERMISSION_GRANTED){

                requireActivity().requestPermissions(arrayOf(Manifest.permission.CALL_PHONE),105)

            }else{
                requireActivity().startActivity(intent)
            }

        }else{
            Toast.makeText(requireContext(),
                getString(R.string.msg_datos),Toast.LENGTH_LONG).show()
        }
    }

    private fun enviarWhatsapp() {
        val telefono = binding.etTelefono.text.toString()
        if(telefono.isNotEmpty()){
            val intent = Intent(Intent.ACTION_VIEW)
            val uri ="whatsapp://sent?phone=506$telefono&text="+getString(R.string.msg_saludos)
            intent.setPackage("com.whatsapp")
            intent.data= Uri.parse(uri)
            startActivity(intent)

        }else{
            Toast.makeText(requireContext(),
                getString(R.string.msg_datos),Toast.LENGTH_LONG).show()
        }
    }
    private fun verWeb() {
        val web = binding.etWeb.text.toString()
        if(web.isNotEmpty()){
            val webpage = Uri.parse("http://$web")
            val intent = Intent(Intent.ACTION_VIEW,webpage)
            startActivity(intent)

        }else{
            Toast.makeText(requireContext(),
                getString(R.string.msg_datos),Toast.LENGTH_LONG).show()
        }
    }
    private fun verMapa() {
        val latitud = binding.tvLatitud.text.toString().toDouble()
        val longitud = binding.tvLongitud.text.toString().toDouble()
        if(latitud.isFinite() && longitud.isFinite()){
            val location = Uri.parse("geo:$latitud,$longitud?z=18")
            val intent = Intent(Intent.ACTION_VIEW,location)
            startActivity(intent)

        }else{
            Toast.makeText(requireContext(),
                getString(R.string.msg_datos),Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId ===R.id.menu_delete){
            deleteLugar()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateLugar() {
        val nombre = binding.etNombre.text.toString()
        val correo = binding.etCorreo.text.toString()
        val telefono= binding.etTelefono.text.toString()
        val web = binding.etWeb.text.toString()
        val lugar = Lugar(args.lugar.id,nombre,correo,telefono,web,0.0,0.0,0.0,"","")
        lugarViewModel.updateLugar(lugar)
        Toast.makeText(requireContext(),getString(R.string.msg_actualizado),Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
    }


    private fun deleteLugar(){
        val builder = AlertDialog.Builder(requireContext())

        builder.setPositiveButton(getString(R.string.si)) {_,_->

            lugarViewModel.deleteLugar(args.lugar)
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
        }

        builder.setNegativeButton(getString(R.string.no)){_,_ ->}
        builder.setTitle(R.string.menu_delete)
        builder.setMessage(getString(R.string.msg_seguro_borrar)+"${args.lugar.nombre}?")
        builder.create().show()



    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}