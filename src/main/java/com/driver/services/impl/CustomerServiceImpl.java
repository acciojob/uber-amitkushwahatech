package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer = customerRepository2.findById(customerId).get();
//		List<TripBooking> tripBookingList  = customer.getTripBookingList();
//		for(TripBooking tripBooking : tripBookingList){
// 			if(tripBooking.getStatus()== TripStatus.CONFIRMED){
//				 tripBooking.setStatus(TripStatus.CANCELED);
//			}
//		}

		customerRepository2.delete(customer);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
//		TripBooking tripBooking = new TripBooking();
//
//		Driver driver= null;
//
//		List<Driver> drivers = driverRepository2.findAll();
//		for(Driver driver1 : drivers){
//			if(driver==null || driver1.getDriverId()<driver.getDriverId()){
//				driver = driver1;
//			}
//		}
//
//		if(driver==null){
//			 throw new Exception("No cab available!");
//		}
//
//		Customer customer =customerRepository2.findById(customerId).get();
//		tripBooking.setCustomer(customer);
//		tripBooking.setDriver(driver);
//		tripBooking.setFromLocation(fromLocation);
//		tripBooking.setToLocation(toLocation);
//		tripBooking.setDistanceInKm(distanceInKm);
//		tripBooking.setStatus(TripStatus.CONFIRMED);

		TripBooking tripBooking=new TripBooking();

		// getting customer obj
		Customer customer=customerRepository2.findById(customerId).get();

		int lowestId=Integer.MAX_VALUE;
		boolean isPresent=false;

		Cab cab=null;


		List<Driver>driverList=driverRepository2.findAll();
		for(Driver driver :driverList){
			if(lowestId>driver.getDriverId()&&driver.getCab().isAvailable()==true){
				lowestId=driver.getDriverId();
				isPresent=true;
				cab=driver.getCab();
			}
		}
		// if no cab is avaliable then throw exception;
		if(isPresent==false) throw new Exception("No cab available!");

		// if we are here then we can say that driver and cab is present;
		cab.setAvailable(false);
		Driver driver= driverRepository2.findById(lowestId).get();


		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		tripBooking.setBill((distanceInKm) * (cab.getPerKmRate()));
		//forigen key
		tripBooking.setDriver( driver);
		tripBooking.setCustomer(customer);

		// question of setting the forigin key

		driver.getTripBookingList().add(tripBooking);
		customer.getTripBookingList().add(tripBooking);

		driverRepository2.save(driver);

		customerRepository2.save(customer);

		return tripBooking;
		// now driver is book for another it is not available show need to false


	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
       TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
	   tripBooking.setBill(0);
	   tripBooking.setStatus(TripStatus.CANCELED);
	   tripBooking.getDriver().getCab().setAvailable(true);
	   tripBookingRepository2.save(tripBooking);


	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.COMPLETED);
//		tripBooking.setBill(0);
		tripBooking.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(tripBooking);

	}
}
