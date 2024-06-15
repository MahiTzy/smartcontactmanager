// package com.scm.smartcontactmanager.controller;

// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.ResponseBody;

// import com.stripe.Stripe;
// import com.stripe.exception.StripeException;
// import com.stripe.model.PaymentIntent;

// import jakarta.annotation.PostConstruct;

// @Controller
// @RequestMapping("/api")
// @ResponseBody
// public class PaymentController {

//     @Value("${stripe.api.key}")
//     private String stripeApiKey;

//     @PostConstruct
//     public void init() {
//         Stripe.apiKey = stripeApiKey;
//     }
    
//     @PostMapping("/payment")
//     public Map<String, Object> createPaymentIntent(@RequestBody Map<String, Object> data) {
//         Map<String, Object> response = new HashMap<>();
//         try {
//             int amount = Integer.parseInt(data.get("amount").toString());
//             // String currency = data.get("currency").toString();
//             // String paymentMethod = data.get("payment_method_types").toString();
//             // String info = data.get("info").toString();
//             // String receipt = data.get("receipt").toString();

//             Map<String, Object> params = new HashMap<>();
//             params.put("amount", amount);
//             params.put("currency", "INR");
//             // params.put("payment_method_types", "card");
//             // params.put("description", info);
//             // params.put("receipt_email", receipt);

            

//             PaymentIntent paymentIntent = PaymentIntent.create(params);

//             response.put("clientSecret", paymentIntent.getClientSecret());
//             System.out.println(paymentIntent);
//         } catch (StripeException e) {
//             response.put("error", e.getMessage());
//         }
//         return response;
//     }
// }
