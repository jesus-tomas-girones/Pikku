package com.example.pikku;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.blautic.pikkuAcademyLib.PikkuAcademy;
import com.blautic.pikkuAcademyLib.ScanInfo;
import com.blautic.pikkuAcademyLib.StatusDevice;
import com.blautic.pikkuAcademyLib.ble.gatt.ConnectionState;
import com.blautic.pikkuAcademyLib.callback.ButtonsCallback;
import com.blautic.pikkuAcademyLib.callback.ConnectionCallback;
import com.blautic.pikkuAcademyLib.callback.ScanCallback;
import com.blautic.pikkuAcademyLib.callback.StatusDeviceCallback;
import com.example.pikku.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    PikkuAcademy pikkuAcademy;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pikkuAcademy = PikkuAcademy.getInstance(this);
        pikkuAcademy.enableLog();
    }

    public void onClickScan(View view) {
        binding.textScan.setText("Pulsa el botón Pikku para ser scaneado");
        pikkuAcademy.scan(true, new ScanCallback() {
            @Override
            public void onScan(ScanInfo scanInfo) {
                pikkuAcademy.saveDevice(scanInfo); // guardar dispositivo para futuras conexiones
                Log.d("Pikku", scanInfo.toString());
                binding.textScan.setText("Scaneado: "+pikkuAcademy.getAddressDevice());
                binding.buttonConnect.setEnabled(true);
            }
        });
    }

    public void onClickConnect(View view) {
        if (binding.buttonConnect.getText().equals("Conectar")) {
            binding.buttonConnect.setText("Desconectar");
            binding.textConnect.setText("Conectando...");
            pikkuAcademy.connect(new ConnectionCallback() {
                @Override
                public void onConnect(ConnectionState state) {
                    if (state == ConnectionState.CONNECTED) {
                        binding.textConnect.setText("Conectado: " + pikkuAcademy.getAddressDevice());
                        binding.buttonConnect.setText("Desconectar");
                        binding.buttonScan.setEnabled(false);
                        binding.buttonEngine.setEnabled(true);
                        binding.buttonLed.setEnabled(true);

                        pikkuAcademy.readStatusDevice(new StatusDeviceCallback() {
                            @Override
                            public void onReadSuccess(StatusDevice statusDevice) {
                                Log.d("Pikku" , statusDevice.toString()); //PROBLEMA: engineOn siempre en false y ledOn siempre en 0
                            }
                        });
                        pikkuAcademy.readButtons(new ButtonsCallback() {
                            @Override
                            public void onReadSuccess(int nButton, boolean pressed, int duration) {
                                Log.d("Pikku" , "botón: "+nButton+" pulsado: "+pressed+" duración: "+duration);

                            }
                        });
                    }
                }
            });
        } else {
            pikkuAcademy.disconnect();
            binding.textConnect.setText("Desconectado");
            binding.buttonConnect.setText("Conectar");
            binding.buttonScan.setEnabled(true);
            binding.buttonEngine.setEnabled(false);
            binding.buttonLed.setEnabled(false);
        }
    }

    public void onClickEngine(View view) {
        if (binding.buttonEngine.getText().equals("Arrancar Motor")) {
            pikkuAcademy.startEngine();
            binding.textEngine.setText("Motor arrancado");
            binding.buttonEngine.setText("Parar Motor");
        } else {
            pikkuAcademy.stopEngine();
            binding.textEngine.setText("Motor parado");
            binding.buttonEngine.setText("Arrancar Motor");
        }
    }

    public void onClickLed(View view) {
        //Inicializar correctamente el texto al conectar
        if (binding.buttonLed.getText().equals("Encender LED")) {
            binding.buttonLed.setText("Apagar LED");
            pikkuAcademy.turnOnLed();
            binding.textLed.setText("LED Ancendido");
            binding.buttonLed.setText("Apagar LED");
        } else {
            pikkuAcademy.turnOffLed();
            binding.textLed.setText("LED Apagado");
            binding.buttonLed.setText("Encender LED");
        }
    }

}