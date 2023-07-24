import React from "react";

const Footer = () => {
  return (
    <footer className="bg-dark text-white py-3 fixed-bottom">
      <div className="container">
        <div className="d-flex flex-column align-items-center">
          <p className="mb-0">&copy; {new Date().getFullYear()} Puzzle Game</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
