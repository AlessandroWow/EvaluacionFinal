package com.stomas.proyectofirebase;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
//Librerias de MQTT y formulario
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class main extends AppCompatActivity {

    //Variables de la conexion a MQTT
    private static String mqttHost = "tcp://prongrazor9742.cloud.shiftr.io:1883"; // IP del Servidor MQTT
    private static String IdUsuario = "AppAndroid"; // Nombre del dispositivo que se conectará

    private static String Topico = "Mensaje"; //Tópico al que se suscribirá
    private static String User = "prongrazor9742"; //Usuario
    private static String Pass = "zo46yQNwqAtWHmhB"; //Contraseña o Token

    //Variable que se utilizará para imprimir los datos del sensor
    private TextView textView;
    private EditText editTextMessage;
    private Button botonEnvio;

    //Libreria MQTT
    private MqttClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        //Enlace
        textView = findViewById(R.id.textView);
        editTextMessage = findViewById(R.id.txtMensaje);
        botonEnvio = findViewById(R.id.botonEnvioMensaje);
        try {
            mqttClient = new MqttClient(mqttHost, IdUsuario, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(User);
            options.setPassword(Pass.toCharArray());
            //Conexion al servidor MQTT
            mqttClient.connect(options);
            //Si se conecta imprimirá un mensaje de MQTT
            Toast.makeText(this,"Aplicación conectada al Servidor MQTT", Toast.LENGTH_SHORT).show();
            //Manejo de entrega de datos y pérdida de conexion
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT", "Conexion perdida");
                }
                //Metodo para envair el mensaje a MQTT
                @Override
                public void messageArrived(String topic, MqttMessage message){
                    String payload = new String(message.getPayload());
                    runOnUiThread(() -> textView.setText(payload));
                }
                //Metodo para verificar si el envio fue exitoso
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT", "Entrega Completa");
                }
            });
        }catch (MqttException e){
            e.printStackTrace();
        }
        //Al dar click en el button enviara el mensaje del topico
        botonEnvio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String mensaje = editTextMessage.getText().toString();
                try {
                    if(mqttClient != null && mqttClient.isConnected()){
                        mqttClient.publish(Topico, mensaje.getBytes(), 0, false);
                        //Mostrar el mensaje enviado en el TextView
                        textView.append("\n - "+ mensaje);
                        Toast.makeText(main.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(main.this, "Error: No se pudo enviar el mensaje. La conexion MQTT no esta activa.", Toast.LENGTH_SHORT).show();
                    }
                }catch (MqttException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
