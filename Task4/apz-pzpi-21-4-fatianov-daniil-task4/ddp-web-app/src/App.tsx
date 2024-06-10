import React from 'react';
import {Routes, Route} from 'react-router-dom';
import {AuthProvider} from './context/AuthContext';
import PrivateRoute from './components/PrivateRoute';
import {CssBaseline} from "@mui/material";
import Authentication from './components/Authentication'
import Header from './components/Header';
import Footer from './components/Footer';
import MainContent from "./components/MainContent";
import { BrowserRouter as Router } from 'react-router-dom';


const App: React.FC = () => {
    return (
        <Router>
            <AuthProvider>
                <CssBaseline/>
                <div className="App">
                    <Header/>
                    <MainContent>
                        <Routes>
                            <Route path="/login" element={<Authentication/>}/>
                            <Route element={<PrivateRoute/>}>
                                {/* Ваші маршрути */}
                            </Route>
                        </Routes>
                    </MainContent>
                    <Footer/>
                </div>
            </AuthProvider>
        </Router>
    );
};

export default App;
