import React, { useState } from "react";
import axios from "axios";

const ImageUploader = ({ onImageUpload, numberOfPieces, handleNumberOfPiecesChange }) => {
  const [selectedImage, setSelectedImage] = useState(null);

  const handleFileChange = (event) => {
    const imageFile = event.target.files[0];
    setSelectedImage(imageFile);
  };

  const handleCreateButtonClick = () => {
    const formData = new FormData();
    formData.append("image", selectedImage);

    axios
        .post("http://localhost:8081/api/divideImage", formData, {
          params: {
            numberOfPieces: numberOfPieces,
          },
        headers: {
          "Content-Type": "multipart/form-data", // Set the content type to multipart/form-data
        },
      })
      .then((response) => {
        console.log("Processed image:", response.data);
      })
      .catch((error) => {
        console.error("Error processing image:", error);
      });
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6 bg-light p-4 rounded">
          <div className="mb-3">
            <label htmlFor="photoInput" className="form-label">
              Upload Photo
            </label>
            <input
              type="file"
              className="form-control"
              id="photoInput"
              accept="image/*"
              onChange={handleFileChange}
            />
          </div>
          <label>Number of Pieces:</label>
          <div className="mb-3">
            <div className="mb-3">
            <div className="form-check form-check-inline">
              <input
                type="radio"
                className="form-check-input"
                name="numberOfPieces"
                value="16"
                checked={numberOfPieces === 16}
                onChange={() => handleNumberOfPiecesChange(16)}
              />
              <label className="form-check-label">16</label>
            </div>
            <div className="form-check form-check-inline">
              <input
                type="radio"
                className="form-check-input"
                name="numberOfPieces"
                value="25"
                checked={numberOfPieces === 25}
                onChange={() => handleNumberOfPiecesChange(25)}
              />
              <label className="form-check-label">25</label>
            </div>
            <div className="form-check form-check-inline">
               <input
                 type="radio"
                 className="form-check-input"
                 name="numberOfPieces"
                 value="36"
                 checked={numberOfPieces === 36}
                 onChange={() => handleNumberOfPiecesChange(36)}
               />
               <label className="form-check-label">36</label>
            </div>
          </div>
          </div>
          <div className="text-center">
            <button className="btn btn-secondary btn-block" onClick={handleCreateButtonClick}>
              Create Puzzle
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ImageUploader;
