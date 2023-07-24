import React from "react";
import { Link } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.css';


const Header = () => {
  return (
    <header className="bg-success text-white py-4">
      <div className="container">
        <div className="d-flex flex-column align-items-center">
          <h1 className="display-4 mb-4">Puzzle Game</h1>
          <nav className="nav">
            <Link to="/" className="nav-link text-white mx-2">Home</Link>
            <Link to="/create-puzzle" className="nav-link text-white mx-2">Create Puzzle</Link>
            <Link to="/puzzle-assembly" className="nav-link text-white mx-2">Puzzle Assembly</Link>
          </nav>
        </div>
      </div>
    </header>
  );
};

export default Header;

