package privacyanalyzer.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.vaadin.spring.annotation.SpringComponent;

import privacyanalyzer.backend.ApkRepository;
import privacyanalyzer.backend.OrderRepository;
import privacyanalyzer.backend.PermissionRepository;
import privacyanalyzer.backend.PickupLocationRepository;
import privacyanalyzer.backend.ProductRepository;
import privacyanalyzer.backend.ProtectionLevelRepository;
import privacyanalyzer.backend.TrackerRepository;
import privacyanalyzer.backend.UserRepository;
import privacyanalyzer.backend.VariablesRepository;
import privacyanalyzer.backend.data.OrderState;
import privacyanalyzer.backend.data.Role;
import privacyanalyzer.backend.data.entity.Customer;
import privacyanalyzer.backend.data.entity.HistoryItem;
import privacyanalyzer.backend.data.entity.Order;
import privacyanalyzer.backend.data.entity.OrderItem;
import privacyanalyzer.backend.data.entity.Permission;
import privacyanalyzer.backend.data.entity.PickupLocation;
import privacyanalyzer.backend.data.entity.Product;
import privacyanalyzer.backend.data.entity.ProtectionLevel;
import privacyanalyzer.backend.data.entity.Tracker;
import privacyanalyzer.backend.data.entity.User;
import privacyanalyzer.backend.data.entity.Variables;

@SpringComponent
public class DataGenerator implements HasLogger {

	private static final String[] FILLING = new String[] { "Strawberry", "Chocolate", "Blueberry", "Raspberry",
			"Vanilla" };
	private static final String[] TYPE = new String[] { "Cake", "Pastry", "Tart", "Muffin", "Biscuit", "Bread", "Bagel",
			"Bun", "Brownie", "Cookie", "Cracker", "Cheese Cake" };
	private static final String[] FIRST_NAME = new String[] { "Ori", "Amanda", "Octavia", "Laurel", "Lael", "Delilah",
			"Jason", "Skyler", "Arsenio", "Haley", "Lionel", "Sylvia", "Jessica", "Lester", "Ferdinand", "Elaine",
			"Griffin", "Kerry", "Dominique" };
	private static final String[] LAST_NAME = new String[] { "Carter", "Castro", "Rich", "Irwin", "Moore", "Hendricks",
			"Huber", "Patton", "Wilkinson", "Thornton", "Nunez", "Macias", "Gallegos", "Blevins", "Mejia", "Pickett",
			"Whitney", "Farmer", "Henry", "Chen", "Macias", "Rowland", "Pierce", "Cortez", "Noble", "Howard", "Nixon",
			"Mcbride", "Leblanc", "Russell", "Carver", "Benton", "Maldonado", "Lyons" };

	private final Random random = new Random(1L);

	private final List<PickupLocation> pickupLocations = new ArrayList<>();
	private final List<Product> products = new ArrayList<>();
	private User baker;
	private User barista;

	@Bean
	public CommandLineRunner loadData(VariablesRepository variablesRepository, OrderRepository orderRepository,
			UserRepository userRepository, ProductRepository productRepository,
			PickupLocationRepository pickupLocationRepository, ApkRepository apkRepository,
			PermissionRepository permissionRepository, ProtectionLevelRepository protectionLevelRepository,
			TrackerRepository trackerRepository, PasswordEncoder passwordEncoder) {
		return args -> {

			getLogger().info("Initialize database");

			if (!hasData(userRepository)) {
				getLogger().info("... generating users");
				createUsers(userRepository, passwordEncoder);
			} else {
				getLogger().info("Users exists");
			}
			/*
			 getLogger().info("... generating products");
			  createProducts(productRepository);
			  getLogger().info("... generating pickup locations");
			  createPickupLocations(pickupLocationRepository);
			  getLogger().info("... generating orders"); createOrders(orderRepository);
			 */
			// apkRepository.count();

			if (!hasData(protectionLevelRepository)) {
			getLogger().info("... loading protection levels");
			createProtectionLevels(protectionLevelRepository);
			} else {
				getLogger().info("protection levels exists");
			}
			
			if (!hasData(permissionRepository)) {
			getLogger().info("... loading permissions");
			createPermissions(permissionRepository, protectionLevelRepository);
			} else {
				getLogger().info("permissions exists");
			}
			
			
			if (!hasData(trackerRepository)) {
			getLogger().info("... loading trackers");
			createTrackers(trackerRepository);
			} else {
				getLogger().info("trackers exists");
			}
			
			if (!hasData(variablesRepository)) {
			getLogger().info("...generating variables");
			createVariables(variablesRepository);
			} else {
				getLogger().info("variables exists");
			}
			
			
			getLogger().info("Finished initializing database");
		};
	}

	private void createVariables(VariablesRepository variablesRepository) {
		Variables defaultVariables = new Variables();

		defaultVariables.setName("default");
		
		defaultVariables.setDeclaredAndNotUsedDangerousPermissionScore(3);
		defaultVariables.setDeclaredAndNotUsedSignatureSystemPermissionScore(1.5);

		defaultVariables.setDeclaredAndUsedDangerousPermissionScore(10);
		defaultVariables.setDeclaredAndUsedSignatureSystemPermissionScore(5);

		defaultVariables.setLibraryDangerousPermissionScore(5);
		defaultVariables.setLibrarySignatureSystemPermissionScore(2.5);

		defaultVariables.setNotDeclaredButUsedDangerousPermissionScore(15);
		defaultVariables.setNotDeclaredButUsedNormalPermissionScore(7.5);
		defaultVariables.setNotDeclaredButUsedSignatureSystemPermissionScore(3.75);

		defaultVariables.setAdbScore(3);
		defaultVariables.setDebuggableScore(8);
		defaultVariables.setMalwareScore(30);
		defaultVariables.setMaximumRiskScore(100);
		defaultVariables.setGreenRisk(25);
		defaultVariables.setOrangRisk(70);
		defaultVariables.setRedRisk(100);
		defaultVariables.setYellowRisk(40);

		variablesRepository.save(defaultVariables);

	}

	private void createTrackers(TrackerRepository trackerRepository) throws IOException {
		File file = new ClassPathResource("trackers.json").getFile();
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(file));
		List<Tracker> data = gson.fromJson(reader, new TypeToken<List<Tracker>>() {
		}.getType());

		for (Tracker t : data) {
			trackerRepository.save(t);
		}

	}

	private void createProtectionLevels(ProtectionLevelRepository protectionLevelRepository) throws IOException {
		File file = new ClassPathResource("protectionlevels.json").getFile();
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(file));
		List<ProtectionLevel> data = gson.fromJson(reader, new TypeToken<List<ProtectionLevel>>() {
		}.getType());

		for (ProtectionLevel p : data) {
			protectionLevelRepository.save(p);
		}

	}

	private void createPermissions(PermissionRepository permissionRepository,
			ProtectionLevelRepository protectionLevelRepository) throws IOException {
		File file = new ClassPathResource("permissions.json").getFile();
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(file));
		List<Permission> data = gson.fromJson(reader, new TypeToken<List<Permission>>() {
		}.getType());

		for (Permission p : data) {
			p.setProtectionlvl(protectionLevelRepository.findByName(p.getProtectionLevel()));
			permissionRepository.save(p);
		}

	}

	private boolean hasData(JpaRepository repo) {
		return repo.count() != 0L;
	}

	private Customer createCustomer() {
		Customer customer = new Customer();
		String first = getRandom(FIRST_NAME);
		String last = getRandom(LAST_NAME);
		customer.setFullName(first + " " + last);
		customer.setPhoneNumber(getRandomPhone());
		if (random.nextInt(10) == 0) {
			customer.setDetails("Very important customer");
		}
		return customer;
	}

	private String getRandomPhone() {
		return "+1-555-" + String.format("%04d", random.nextInt(10000));
	}

	private void createOrders(OrderRepository orderRepository) {
		int yearsToInclude = 2;
		List<Order> orders = new ArrayList<>();

		LocalDate now = LocalDate.now();
		LocalDate oldestDate = LocalDate.of(now.getYear() - yearsToInclude, 1, 1);
		LocalDate newestDate = now.plusMonths(1L);

		for (LocalDate dueDate = oldestDate; dueDate.isBefore(newestDate); dueDate = dueDate.plusDays(1)) {
			// Create a slightly upwards trend - everybody wants to be
			// successful
			int relativeYear = dueDate.getYear() - now.getYear() + yearsToInclude;
			int relativeMonth = relativeYear * 12 + dueDate.getMonthValue();
			double multiplier = 1.0 + 0.03 * relativeMonth;
			int ordersThisDay = (int) (random.nextInt(10) + 1 * multiplier);
			for (int i = 0; i < ordersThisDay; i++) {
				orders.add(createOrder(dueDate));
			}
		}
		orderRepository.save(orders);
	}

	private Order createOrder(LocalDate dueDate) {
		Order order = new Order();

		order.setCustomer(createCustomer());
		order.setPickupLocation(getRandomPickupLocation());
		order.setDueDate(dueDate);
		order.setDueTime(getRandomDueTime());
		order.setState(getRandomState(order.getDueDate()));

		int itemCount = random.nextInt(3);
		List<OrderItem> items = new ArrayList<>();
		for (int i = 0; i <= itemCount; i++) {
			OrderItem item = new OrderItem();
			Product product;
			do {
				product = getRandomProduct();
			} while (containsProduct(items, product));
			item.setProduct(product);
			item.setQuantity(random.nextInt(10) + 1);
			if (random.nextInt(5) == 0) {
				if (random.nextBoolean()) {
					item.setComment("Lactose free");
				} else {
					item.setComment("Gluten free");
				}
			}
			items.add(item);
		}
		order.setItems(items);

		order.setHistory(createOrderHistory(order));

		return order;
	}

	private List<HistoryItem> createOrderHistory(Order order) {
		ArrayList<HistoryItem> history = new ArrayList<>();
		HistoryItem item = new HistoryItem(getBarista(), "Order placed");
		item.setNewState(OrderState.NEW);
		LocalDateTime orderPlaced = order.getDueDate().minusDays(random.nextInt(5) + 2L).atTime(random.nextInt(10) + 7,
				00);
		item.setTimestamp(orderPlaced);
		history.add(item);
		if (order.getState() == OrderState.CANCELLED) {
			item = new HistoryItem(getBarista(), "Order cancelled");
			item.setNewState(OrderState.CANCELLED);
			item.setTimestamp(orderPlaced.plusDays(random
					.nextInt((int) orderPlaced.until(order.getDueDate().atTime(order.getDueTime()), ChronoUnit.DAYS))));
			history.add(item);
		} else if (order.getState() == OrderState.CONFIRMED || order.getState() == OrderState.DELIVERED
				|| order.getState() == OrderState.PROBLEM || order.getState() == OrderState.READY) {
			item = new HistoryItem(getBaker(), "Order confirmed");
			item.setNewState(OrderState.CONFIRMED);
			item.setTimestamp(orderPlaced.plusDays(random.nextInt(2)).plusHours(random.nextInt(5)));
			history.add(item);

			if (order.getState() == OrderState.PROBLEM) {
				item = new HistoryItem(getBaker(), "Can't make it. Did not get any ingredients this morning");
				item.setNewState(OrderState.PROBLEM);
				item.setTimestamp(order.getDueDate().atTime(random.nextInt(4) + 4, 0));
				history.add(item);
			} else if (order.getState() == OrderState.READY || order.getState() == OrderState.DELIVERED) {
				item = new HistoryItem(getBaker(), "Order ready for pickup");
				item.setNewState(OrderState.READY);
				item.setTimestamp(order.getDueDate().atTime(random.nextInt(2) + 8, random.nextBoolean() ? 0 : 30));
				history.add(item);
				if (order.getState() == OrderState.DELIVERED) {
					item = new HistoryItem(getBaker(), "Order delivered");
					item.setNewState(OrderState.DELIVERED);
					item.setTimestamp(order.getDueDate().atTime(order.getDueTime().minusMinutes(random.nextInt(120))));
					history.add(item);
				}
			}
		}

		return history;
	}

	private boolean containsProduct(List<OrderItem> items, Product product) {
		for (OrderItem item : items) {
			if (item.getProduct() == product) {
				return true;
			}
		}
		return false;
	}

	private LocalTime getRandomDueTime() {
		int time = 8 + 4 * random.nextInt(3);

		return LocalTime.of(time, 0);
	}

	private OrderState getRandomState(LocalDate due) {
		LocalDate today = LocalDate.now();
		LocalDate tomorrow = today.plusDays(1);
		LocalDate twoDays = today.plusDays(2);

		if (due.isBefore(today)) {
			if (random.nextDouble() < 0.9) {
				return OrderState.DELIVERED;
			} else {
				return OrderState.CANCELLED;
			}
		} else {
			if (due.isAfter(twoDays)) {
				return OrderState.NEW;
			} else if (due.isAfter(tomorrow)) {
				// in 1-2 days
				double resolution = random.nextDouble();
				if (resolution < 0.8) {
					return OrderState.NEW;
				} else if (resolution < 0.9) {
					return OrderState.PROBLEM;
				} else {
					return OrderState.CANCELLED;
				}
			} else {
				double resolution = random.nextDouble();
				if (resolution < 0.6) {
					return OrderState.READY;
				} else if (resolution < 0.8) {
					return OrderState.DELIVERED;
				} else if (resolution < 0.9) {
					return OrderState.PROBLEM;
				} else {
					return OrderState.CANCELLED;
				}
			}

		}
	}

	private Product getRandomProduct() {
		double cutoff = 2.5;
		double g = random.nextGaussian();
		g = Math.min(cutoff, g);
		g = Math.max(-cutoff, g);
		g += cutoff;
		g /= (cutoff * 2.0);

		return products.get((int) (g * (products.size() - 1)));
	}

	private PickupLocation getRandomPickupLocation() {
		return getRandom(pickupLocations);
	}

	private User getBaker() {
		return baker;
	}

	private User getBarista() {
		return barista;
	}

	private <T> T getRandom(List<T> items) {
		return items.get(random.nextInt(items.size()));
	}

	private <T> T getRandom(T[] array) {
		return array[random.nextInt(array.length)];
	}

	private void createPickupLocations(PickupLocationRepository pickupLocationRepository) {
		PickupLocation store = new PickupLocation();
		store.setName("Store");
		pickupLocations.add(pickupLocationRepository.save(store));
		PickupLocation bakery = new PickupLocation();
		bakery.setName("Bakery");
		pickupLocations.add(pickupLocationRepository.save(bakery));
	}

	private void createProducts(ProductRepository productRepository) {
		for (int i = 0; i < 10; i++) {
			Product product = new Product();
			product.setName(getRandomProductName());
			double doublePrice = 2.0 + random.nextDouble() * 100.0;
			product.setPrice((int) (doublePrice * 100.0));
			products.add(productRepository.save(product));
		}
	}

	private String getRandomProductName() {
		String firstFilling = getRandom(FILLING);
		String name;
		if (random.nextBoolean()) {
			String secondFilling;
			do {
				secondFilling = getRandom(FILLING);
			} while (secondFilling.equals(firstFilling));

			name = firstFilling + " " + secondFilling;
		} else {
			name = firstFilling;
		}
		name += " " + getRandom(TYPE);

		return name;
	}

	private void createUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		baker = userRepository
				.save(new User("user1@vaadin.com", "mioann47", passwordEncoder.encode("pass123"), Role.USER));
		User user = new User("user@vaadin.com", "Modestos", passwordEncoder.encode("pass123"), Role.USER);
		user.setLocked(true);
		barista = userRepository.save(user);
		user = new User("admin@admin.com", "Admin", passwordEncoder.encode("admin"), Role.ADMIN);
		user.setLocked(true);
		userRepository.save(user);
		user = new User("guest", "guest", passwordEncoder.encode("guest"), Role.GUEST);
		user.setLocked(true);
		userRepository.save(user);
	}
}
