# Ecommerce-App (Spring Boot)

The Ecommerce-App is a robust backend service, crafted using Spring Boot, that serves as the backbone for a fully functional eCommerce platform. It features essential functionalities such as user role-based authentication, product categorization, shopping cart management, order processing, and secure payments. The architecture is designed for scalability and ease of integration with any front-end framework.

## Features

1. **User Authentication & Authorization**
   - Implements role-based authentication ensuring secure access management for various user types (Admin, Customer).
   - Utilizes cookie-based session handling to provide seamless and secure user authentication across multiple sessions.

2. **Product & Category Management**
   - Supports hierarchical product management with category-wise organization, facilitating a streamlined shopping experience.
   - Admins can add, update, or remove products dynamically within designated categories for better content management.

3. **Shopping Cart**
   - Full-fledged shopping cart functionality that allows users to add, update, or remove products with real-time updates.
   - Ensures that the cart contents are persistent until the user completes or modifies the order.

4. **Address Management**
   - Users can manage and store multiple shipping addresses within their profile, simplifying the checkout process.
   - During checkout, users can easily choose their preferred shipping address from the stored list.

5. **Order Management & Payments**
   - Provides a seamless order placement system, integrating the cart, user details, and shipping information.
   - Includes a secure payment gateway to facilitate smooth transactions, ensuring the integrity of payment data.

## API Structure

The backend is organized into modular and RESTful controllers, each dedicated to handling specific aspects of the system:

### Auth Controller
- **Sign In**
  - **Endpoint:** `POST /signin`
  - **Description:** Authenticates a user and returns an authentication token. Requires a JSON body with username and password.

- **Sign Up**
  - **Endpoint:** `POST /signup`
  - **Description:** Registers a new user. Requires a JSON body with user details.

- **Get Username**
  - **Endpoint:** `GET /username`
  - **Description:** Retrieves the username of the currently authenticated user.

- **Get User Details**
  - **Endpoint:** `GET /user`
  - **Description:** Retrieves details of the currently authenticated user.

- **Sign Out**
  - **Endpoint:** `POST /signout`
  - **Description:** Logs out the current user and invalidates the authentication token.

---

### Category Controller
- **Get All Categories**
  - **Endpoint:** `GET /public/category`
  - **Description:** Retrieves a list of all categories.
  
- **Get Category by ID**
  - **Endpoint:** `GET /public/category/{categoryId}`
  - **Description:** Retrieves details of a specific category based on the provided `categoryId`.
  
- **Create New Category**
  - **Endpoint:** `POST /public/category`
  - **Description:** Adds a new category. Requires a JSON body with category details.
  
- **Delete Category**
  - **Endpoint:** `DELETE /public/category/{categoryId}`
  - **Description:** Deletes a category identified by `categoryId`.
  
- **Update Category**
  - **Endpoint:** `PUT /public/category/{categoryId}`
  - **Description:** Updates the details of an existing category. Requires a JSON body with updated information.

---

### Product Controller
- **Add Product to Category**
  - **Endpoint:** `POST /admin/categories/{categoryId}/products`
  - **Description:** Adds a new product to a specified category. Requires a JSON body with product details.
  
- **Get All Products**
  - **Endpoint:** `GET /public/products`
  - **Description:** Retrieves a list of all products.
  
- **Get Products by Category**
  - **Endpoint:** `GET /public/categories/{categoryId}/products`
  - **Description:** Retrieves all products under a specified category.
  
- **Search Products by Keyword**
  - **Endpoint:** `GET /public/products/keyword/{keyword}`
  - **Description:** Retrieves products that match the specified keyword.
  
- **Update Product**
  - **Endpoint:** `PUT /admin/products/{productId}`
  - **Description:** Updates the details of an existing product. Requires a JSON body with updated information.
  
- **Delete Product**
  - **Endpoint:** `DELETE /admin/products/{productId}`
  - **Description:** Deletes a product identified by `productId`.
  
- **Update Product Image**
  - **Endpoint:** `PUT /admin/products/{productId}/image`
  - **Description:** Updates the image of a specified product.

---

### Cart Controller
- **Add Product to Cart**
  - **Endpoint:** `POST /carts/products/{productId}/quantity/{quantity}`
  - **Description:** Adds a specified quantity of a product to the cart.

- **Get All Carts**
  - **Endpoint:** `GET /carts`
  - **Description:** Retrieves a list of all shopping carts.

- **Get User's Cart**
  - **Endpoint:** `GET /carts/user/cart`
  - **Description:** Retrieves the shopping cart of the currently authenticated user.

- **Update Product Quantity in Cart**
  - **Endpoint:** `PUT /cart/products/{productId}/quantity/{operations}`
  - **Description:** Updates the quantity of a specific product in the cart based on the provided operation (e.g., increase or decrease).

- **Remove Product from Cart**
  - **Endpoint:** `DELETE /carts/{cartId}/product/{productId}`
  - **Description:** Removes a specified product from the cart identified by `cartId`.

---

### Address Controller
- **Add Address**
  - **Endpoint:** `POST /addresses`
  - **Description:** Creates a new address for a user. Requires a JSON body with address details.

- **Get All Addresses**
  - **Endpoint:** `GET /addresses`
  - **Description:** Retrieves a list of all addresses.

- **Get User Addresses**
  - **Endpoint:** `GET /users/addresses`
  - **Description:** Retrieves addresses associated with the currently authenticated user.

- **Get Address by ID**
  - **Endpoint:** `GET /addresses/{addressId}`
  - **Description:** Retrieves details of a specific address identified by `addressId`.

- **Update Address**
  - **Endpoint:** `PUT /addresses/{addressId}`
  - **Description:** Updates the details of an existing address. Requires a JSON body with updated address information.

- **Delete Address**
  - **Endpoint:** `DELETE /addresses/{addressId}`
  - **Description:** Deletes an address identified by `addressId`.

---

### Order Controller
- **Create Order with Payment Method**
  - **Endpoint:** `POST /order/users/payments/{paymentMethod}`
  - **Description:** Creates a new order and processes the payment using the specified payment method. Requires order details in the JSON body.

---

## Technology Stack
The Ecommerce-App utilizes the following technologies:
- Java 22
- Spring Boot 3.3.4 for backend architecture
- Spring Security for user authentication and authorization
- Hibernate/JPA for ORM and database interaction
- MySQL for persistent data storage
- Maven for dependency management and build automation

## Installation and Setup
Clone the repository:

```bash
git clone https://github.com/rishraks/Ecommerce-App.git
cd Ecommerce-App
./mvnw clean install
./mvnw spring-boot:run
```
The backend server will start at http://localhost:8080

## Contribution Guidelines
Contributions are encouraged and appreciated! To contribute:
- Fork the repository.
- Create a new feature branch.
- Submit a pull request with a detailed description of the changes made.
  
## License
This project is licensed under the MIT License. For more information, please refer to the LICENSE file.

## Contact
For any queries or support regarding the project, feel free to reach out via the repository's issue tracker or submit a pull request.
