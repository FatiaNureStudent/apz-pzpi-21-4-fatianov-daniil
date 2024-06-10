import React, { createContext, useState, ReactNode, useContext } from 'react';
import { AuthService } from '../services/AuthService';

interface AuthContextProps {
    isAuthenticated: boolean;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
}

const AuthContext = createContext<AuthContextProps | undefined>(undefined);

const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);

    const login = async (email: string, password: string) => {
        const result = await AuthService.login(email, password);
        setIsAuthenticated(result.isAuthenticated);
        if (!result.isAuthenticated) {
            console.error('Login failed or user is not an administrator.');
        }
    };

    const logout = () => {
        localStorage.removeItem('user');
        setIsAuthenticated(false);
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export { AuthContext, AuthProvider };