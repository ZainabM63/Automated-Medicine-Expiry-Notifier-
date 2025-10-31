import React from 'react';
import ProductList from './components/ProductList';
import './/App.css';
import './/style.css';

const ExpiryProducts = ({ expiredProducts, nearExpiryProducts, handleDeleteProduct, error }) => {
    return (
        <div className="app-container">
            <h1 className="app-title">Expiry Management System</h1>
            {error && <p className="error-message">{error}</p>}
            <ProductList 
                products={expiredProducts} 
                title="Expired Products" 
                onDelete={(batchNumber) => handleDeleteProduct(batchNumber, 'expired')} 
            />
            <ProductList 
                products={nearExpiryProducts} 
                title="Near-Expiry Products" 
                onDelete={(batchNumber) => handleDeleteProduct(batchNumber, 'nearExpiry')} 
            />
        </div>
    );
};

export default ExpiryProducts;
