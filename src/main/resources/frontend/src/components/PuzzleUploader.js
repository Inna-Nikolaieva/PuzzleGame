import React, { useState } from "react";

const PuzzleUploader = ({ onImageUpload }) => {
  const [selectedImages, setSelectedImages] = useState([]);

  const handleFileChange = (event) => {
    const selectedFiles = Array.from(event.target.files);
    setSelectedImages(selectedFiles);
  };

  const handleUploadButtonClick = () => {
    onImageUpload(selectedImages);
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6 bg-light p-4 rounded">
          <div className="mb-3">
            <label htmlFor="photoInput" className="form-label">
              Upload Puzzle Images
            </label>
            <input
              type="file"
              className="form-control"
              id="photoInput"
              accept="image/*"
              onChange={handleFileChange}
              multiple
            />
          </div>
          <div className="text-center">
            <button className="btn btn-secondary btn-block" onClick={handleUploadButtonClick}>
              Upload Images
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PuzzleUploader;
