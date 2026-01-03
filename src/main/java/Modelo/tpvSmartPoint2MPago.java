
package Modelo;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class tpvSmartPoint2MPago {

    private static final String ACCESS_TOKEN = "TEST-8844685058100286-061019-a8393f05014df79bd31aa42099b0e266-688608434";

 /*   public static void realizarPago(double monto, String referencia) {
        OkHttpClient client = new OkHttpClient();

        String json = "{\n" +
                "  \"external_reference\": \"" + referencia + "\",\n" +
                "  \"amount\": " + monto + ",\n" +
                "  \"payment_methods\": [\"debit_card\", \"credit_card\"]\n" +
                "}";

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(json, mediaType);

        Request request = new Request.Builder()
                .url("https://api.mercadopago.com/point/integrations")
                .post(body)
                .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

                String status = jsonResponse.has("status") ? jsonResponse.get("status").getAsString() : "desconocido";
                String message;

                switch (status) {
                    case "approved":
                        message = "✅ Pago aprobado correctamente.";
                        break;
                    case "in_process":
                        message = "⏳ Pago pendiente. El cliente debe completar la acción.";
                        break;
                    case "rejected":
                        message = "❌ Pago rechazado. Verificar tarjeta o saldo.";
                        break;
                    default:
                        message = "⚠️ Estado del pago desconocido: " + status;
                        break;
                }

                System.out.println(message);
                System.out.println("Respuesta completa: " + responseBody);
            } else {
                System.err.println("Error HTTP: " + response.code());
                if (response.body() != null) {
                    System.err.println("Respuesta de error: " + response.body().string());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
}
