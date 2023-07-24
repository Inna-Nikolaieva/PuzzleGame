import React from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";

const HomePage = () => {
  return (
    <div>
      <Header />
      <main className="bg-light py-5">
        <div className="container">
          <div className="row">
            <div className="col-md-6">
              <img
                src="../Puzzle-Step.png"
                alt="Puzzle Game"
                className="img-fluid rounded"
              />
            </div>
            <div className="col-md-6 d-flex align-items-center">
              <div>
                <h2 className="mb-4">Welcome to the Puzzle Game!</h2>
                <p className="lead mb-4">
                  This is a fun and challenging puzzle game where you can create your own puzzles from your photos or use existing examples.
                </p>
                <p className="mb-4">
                  To create your own puzzle, simply upload a photo. If you don't have a photo, you can use one of the existing examples.
                </p>
                <p>
                  We hope you enjoy playing the Puzzle Game!
                </p>
              </div>
            </div>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default HomePage;
