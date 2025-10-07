package com.aditya.project.service;

import com.aditya.project.exception.APIException;
import com.aditya.project.exception.ResourceNotFoundException;
import com.aditya.project.model.Cart;
import com.aditya.project.model.CartItem;
import com.aditya.project.model.Product;
import com.aditya.project.payload.CartDTO;
import com.aditya.project.payload.ProductDTO;
import com.aditya.project.repository.CartItemRepository;
import com.aditya.project.repository.CartRepository;
import com.aditya.project.repository.ProductRepository;
import com.aditya.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtil authUtil;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));

        // perform validations
        CartItem cartItem = cartItemRepository
                .findCartItemByProductIdAndCartId(productId, cart.getCartId());
        if(cartItem != null){
            throw new APIException("Product " + product.getProductName() + ", already exists in cart");
        }
        if(product.getQuantity() == 0){
            throw new APIException(product.getProductName() + " is not available");
        }
        if(product.getQuantity() < quantity){
            throw new APIException("Please make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity());
        }

        // create cart item
        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        // save the cart item
        cartItemRepository.save(newCartItem);

        // update the cart
        cart.getCartItems().add(newCartItem);
        cart.setTotalPrice(cart.getTotalPrice() + product.getSpecialPrice() * quantity);
        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        });

        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }

    private Cart createCart(){  // helper
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null) return userCart;

        Cart cart = new Cart();
        cart.setUser(authUtil.loggedInUser());
        cart.setTotalPrice(0.00);
        return cartRepository.save(cart);
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if(carts.isEmpty()) {
            throw new APIException("Cart is empty");
        }

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> productDTOs = cart.getCartItems().stream()
                    .map(ci -> {
                        ProductDTO productDTO = modelMapper.map(ci.getProduct(), ProductDTO.class);
                        productDTO.setQuantity(ci.getQuantity());
                        return productDTO;
                    })
                    .toList();

            cartDTO.setProducts(productDTOs);
            return cartDTO;
        }).toList();

        return cartDTOs;
    }

    @Override
    public CartDTO getUsersCart(String email, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndUserId(email, cartId);
        if(cart == null) {
            throw new ResourceNotFoundException("Cart", "cardId", cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(ci -> ci.getProduct().setQuantity(ci.getQuantity()));
        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(ci -> modelMapper.map(ci.getProduct(), ProductDTO.class))
                .toList();
        cartDTO.setProducts(productDTOs);
        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String email = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(email);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart", "cardId", cartId));
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));

        if(product.getQuantity() == 0){
            throw new APIException(product.getProductName() + " is not available");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cartId);
        if(cartItem == null){
            throw new APIException("Product " + product.getProductName() + " is not available in the cart!");
        }

        int newQuantity = cartItem.getQuantity() + quantity;

        if(newQuantity == 0){
            deleteProductFromCart(cartId, productId);
        }else {
            cartItem.setDiscount(product.getDiscount());
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);

            cart.setTotalPrice(cart.getTotalPrice() + cartItem.getProductPrice() * quantity);

            cartRepository.save(cart);
        }


        CartItem updatedItem = cartItemRepository.save(cartItem);

        if(updatedItem.getQuantity() == 0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(ci -> {
                    ProductDTO productDTO = modelMapper.map(ci.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(ci.getQuantity());
                    return productDTO;
                }).toList();
        cartDTO.setProducts(productDTOs);
        return cartDTO;
    }

    @Override
    @Transactional
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart", "cartId", cartId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cartId);
        if(cartItem == null){
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() - cartItem.getProductPrice() * cartItem.getQuantity());
        cartItemRepository.deleteCartItemByProductIdAndCartId(productId, cartId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart!";
    }

    @Override
    public void updateProductInCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart", "cardId", cartId));
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cartId);
        if(cartItem == null){
            throw new APIException("Product " + product.getProductName() + " is not available in the cart!");
        }

        // reducing the previous value
        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());
        // setting the new value
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.save(cartItem);
    }
}
