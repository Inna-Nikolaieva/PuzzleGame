import React, { useState } from "react";
import axios from 'axios';
import Header from "../components/Header";
import Footer from "../components/Footer";
import ImageUploader from "../components/ImageUploader"

const BreakPicturePage = () => {
  const [numberOfPieces, setNumberOfPieces] = useState(16);
    const [processedImage, setProcessedImage] = useState(null);

    const handleImageUpload = (imageFile) => {
      // Create a FormData object to send the image and the selected number of pieces
      const formData = new FormData();
      formData.append('image', imageFile);
      formData.append('numberOfPieces', numberOfPieces);

      // Make a POST request to the server using Axios
      axios
        .post('http://localhost:8081/api/divideImage', formData)
        .then((response) => {
          // Handle the response from the server (e.g., show the processed image)
          console.log('Processed image:', response.data);
          setProcessedImage(response.data); // Assuming the server returns the processed image URL
        })
        .catch((error) => {
          // Handle any errors
          console.error('Error processing image:', error);
        });
    };

  const handleNumberOfPiecesChange = (selectedNumberOfPieces) => {
    setNumberOfPieces(selectedNumberOfPieces);
  };

  return (
    <div>
      <Header />
      <ImageUploader
        onImageUpload={handleImageUpload}
        numberOfPieces={numberOfPieces}
        handleNumberOfPiecesChange={handleNumberOfPiecesChange}
      />
      <main className="d-flex justify-content-center align-items-center" style={{ minHeight: "40vh" }}>
        <div>
          <div className="my-4">
            <h4>How to create your own puzzle:</h4>
            <ol>
              <li>Upload a photo.</li>
              <li>Choose the number of pieces you want the puzzle to have.</li>
              <li>Click "Create Puzzle".</li>
            </ol>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default BreakPicturePage;

