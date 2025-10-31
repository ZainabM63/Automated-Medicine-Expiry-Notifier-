import React, { useEffect, useState } from 'react';
import axios from 'axios';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import ProductList from './components/ProductList';
import './App.css';
import './style.css';

const App = () => {
    const [expiredProducts, setExpiredProducts] = useState([]);
    const [nearExpiryProducts, setNearExpiryProducts] = useState([]);
    const [error, setError] = useState(null);
    const [stompClient, setStompClient] = useState(null);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const expiredResponse = await axios.get('http://localhost:8080/api/products/expired');
                setExpiredProducts(expiredResponse.data);

                const nearExpiryResponse = await axios.get('http://localhost:8080/api/products/near-expiry');
                setNearExpiryProducts(nearExpiryResponse.data);
            } catch (error) {
                setError('Error fetching products');
                console.error('Error fetching products:', error);
            }
        };

        fetchProducts();

        const socket = new SockJS('http://localhost:8080/ws');
        const client = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
                console.log('Connected to WebSocket');

                client.subscribe('/topic/expired', (message) => {
                    console.log('Received expired products update:', message.body);
                    const updatedProducts = JSON.parse(message.body);
                    setExpiredProducts(prevProducts => {
                        const productMap = new Map(prevProducts.map(product => [product.batchNumber, product]));
                        updatedProducts.forEach(product => productMap.set(product.batchNumber, product));
                        return Array.from(productMap.values());
                    });
                });

                client.subscribe('/topic/near-expiry', (message) => {
                    console.log('Received near-expiry products update:', message.body);
                    const updatedProducts = JSON.parse(message.body);
                    setNearExpiryProducts(prevProducts => {
                        const productMap = new Map(prevProducts.map(product => [product.batchNumber, product]));
                        updatedProducts.forEach(product => productMap.set(product.batchNumber, product));
                        return Array.from(productMap.values());
                    });
                });
            },
            onWebSocketError: (error) => {
                console.error('WebSocket error:', error);
                setError('WebSocket error');
            },
        });

        client.activate();
        setStompClient(client);

        return () => {
            if (client) {
                client.deactivate();
            }
        };
    }, []);

    const handleDeleteProduct = async (batchNumber, listType) => {
        try {
            await axios.delete(`http://localhost:8080/api/products/${batchNumber}`);
            if (listType === 'expired') {
                setExpiredProducts(prevProducts => prevProducts.filter(product => product.batchNumber !== batchNumber));
            } else if (listType === 'nearExpiry') {
                setNearExpiryProducts(prevProducts => prevProducts.filter(product => product.batchNumber !== batchNumber));
            }
            console.log(`Product with batch number ${batchNumber} deleted successfully.`);
        } catch (error) {
            console.error('Error deleting product:', error);
            setError('Error deleting product');
        }
    };

    return (
        <div className="app-container">
            <header className="header-bar">
                <div className="logo-section">
                    <img src="/logo192.png" alt="App Logo" className="logo" />
                    <h1 className="app-title">Expiry Management System</h1>
                </div>
                <button className="refresh-button" onClick={() => window.location.reload()}>
                    ⟳ Refresh
                </button>
            </header>

            {error && <p className="error-message">{error}</p>}

            <div className="summary-cards">
                <div className="card expired-card">
                    <h2>{expiredProducts.length}</h2>
                    <p>Expired Products</p>
                </div>
                <div className="card near-card">
                    <h2>{nearExpiryProducts.length}</h2>
                    <p>Near-Expiry Products</p>
                </div>
            </div>

            <div className="product-lists">
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
        </div>
    );
};

export default App;
