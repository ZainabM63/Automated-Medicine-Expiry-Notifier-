// src/components/Product.js
import React from 'react';
import './ProductList.css';

const ProductList = ({ products, title, onDelete, onSelect, selected }) => {
  return (
    <div className="product-list">
      <h2 className="list-title">{title}</h2>
      {products.length === 0 ? (
        <p className="empty-msg">No products found.</p>
      ) : (
        <table className="product-table">
          <thead>
            <tr>
              <th>Select</th>
              <th>Batch #</th>
              <th>Name</th>
              <th>Expiry Date</th>
              <th>Days Left</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {products.map((p) => (
              <tr key={p.batchNumber}>
                <td>
                  <input
                    type="checkbox"
                    checked={selected.includes(p.batchNumber)}
                    onChange={() => onSelect(p.batchNumber)}
                  />
                </td>
                <td>{p.batchNumber}</td>
                <td>{p.name}</td>
                <td>{p.expiryDate}</td>
                <td>{p.daysLeft}</td>
                <td>
                  <button className="btn delete" onClick={() => onDelete(p.batchNumber)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ProductList;
