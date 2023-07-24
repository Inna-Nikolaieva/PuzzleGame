import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import HomePage from './pages/HomePage';
import BreakPicturePage from './pages/BreakPicturePage';
import ManualAssemblyPage from './pages/ManualAssemblyPage';

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/create-puzzle" element={<BreakPicturePage />} />
        <Route path="/puzzle-assembly" element={<ManualAssemblyPage />} />
      </Routes>
    </Router>
  );
};

export default App;




