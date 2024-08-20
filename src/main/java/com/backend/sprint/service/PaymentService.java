package com.backend.sprint.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.sprint.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;

@Service
public class PaymentService {

	@Value("${stripe.api.key}")
	private String apiKey;

	@Autowired
	private UserRepository userRepository;

	public void createStripeCustomer(String email, String username, long userId) {

		Stripe.apiKey = apiKey;
		CustomerCreateParams params = CustomerCreateParams.builder().setName(username).setEmail(email).build();
		try {
			Customer customer = Customer.create(params);
			userRepository.setUserStripeId(customer.getId(), userId);
		} catch (StripeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
