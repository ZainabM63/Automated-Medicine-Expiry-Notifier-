import React from 'react';
; // Ensure app.css is imported
 // Ensure style.css is imported

const ProductList = ({ products, title, onDelete }) => {
    const containerClass = title.includes('Expired') ? 'product-list-container expired-products' : 'product-list-container near-expiry-products';

    // Function to format the expiry date if it's a Date object
    const formatExpiryDate = (expiryDate) => {
        return expiryDate instanceof Date ? expiryDate.toLocaleDateString() : expiryDate;
    };

    return (
        <div className={containerClass}>
            <h2 className="product-list-title">{title}</h2>
            {products.length === 0 ? (
                <p>No {title.toLowerCase()} found.</p>
            ) : (
                <ul className="product-list">
                    {products.map(product => (
                        <li key={product.batchNumber} className="product-item">
                            <strong>{product.name}</strong> (Batch: {product.batchNumber}, Expiry: {formatExpiryDate(product.expiryDate)})
                            <button 
                                className="delete-button" 
                                onClick={() => onDelete(product.batchNumber)}
                                aria-label={`Delete product with batch number ${product.batchNumber}`}
                            >
                                Delete
                            </button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default ProductList;
