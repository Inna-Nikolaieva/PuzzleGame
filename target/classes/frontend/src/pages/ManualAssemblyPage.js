import React, { useState, useMemo, useRef } from "react";
import axios from "axios";
import Header from "../components/Header";
import Footer from "../components/Footer";
import PuzzleUploader from "../components/PuzzleUploader";
import VerifyPuzzle from "../components/VerifyPuzzle";

const ManualAssemblyPage = () => {

     const [assembledPuzzles, setAssembledPuzzles] = useState([]);
     const [selectedImages, setSelectedImages] = useState([]);
     const [puzzleData, setPuzzleData] = useState([]);
     const [assembledPuzzles, setAssembledPuzzles] = useState([]);

     const handleImageUpload = (images) => {
       setSelectedImages(images);

           const newPuzzleData = images.map((image, index) => ({
             src: URL.createObjectURL(image),
             name: `Piece ${index + 1}`,
             position: [Math.random() * 300, Math.random() * 300 + 300],
           }));

           setPuzzleData(newPuzzleData);
     };


     const pieces = useMemo(() => {

        return puzzleData;
    }, [puzzleData]);

      const selected = useRef();

          const handleMouseDown = (event, index) => {
              const {offsetX, offsetY} = event.nativeEvent;
              selected.current = {index, element: event.target, offsetX, offsetY};
              document.addEventListener("mousemove", handleMouseMove);
          };

          const handleMouseMove = (event) => {
              const {index, element, offsetX, offsetY} = selected.current;
              const positionX = event.pageX - offsetX;
              const positionY = event.pageY - offsetY;
              pieces[index].position = [positionX, positionY];
              element.style.left = `${positionX}px`;
              element.style.top = `${positionY}px`;
          };

          const handleMouseUp = () => {
              const sortedPieces = sortPiecesByPosition(pieces);
              console.log(sortedPieces.map(sortedPiece => sortedPiece.index))
              setAssembledPuzzles(sortedPieces);
              endDrag();
          };

          const endDrag = () => {
              selected.current = null;
              document.removeEventListener("mousemove", handleMouseMove);
          };


      const sortPiecesByPosition = (pieces) => {
        return [...pieces].sort((a, b) => {
          const [ax, ay] = a.position;
          const [bx, by] = b.position;

          if (Math.abs(ay - by) > 5) {
            return ay - by;
          }

          if (Math.abs(ax - bx) > 5) {
            return ax - bx;
          }

          return 0;
        });
      };

      async function handleAutoCompletePuzzles() {
          try {
                let base64Images = assembledPuzzles.map((d) => d.image.src.split(',')[1]);

                const response = await axios.post('http://localhost:8081/api/autoCompletePuzzles', base64Images);

                const assembledImageUrl = response.data.imageUrl;

                setAssembledImageUrl(assembledImageUrl);
              } catch (error) {
                console.error('Error verifying puzzles:', error);
              }
          }


    return (
      <div>
        <Header />
        <main className="container mt-5">
          <PuzzleUploader onImageUpload={handleImageUpload} />
          <div className="row justify-content-center">
            <div className="col-md-6">
              {/* Area to display loaded puzzles */}
              <div className="card mb-3">
                <div className="card-header">Assemble Puzzles</div>
                <div className="card-body" style={{ minHeight: "400px" }}>
                     {assembledImageUrl && (
                        <img
                          src={assembledImageUrl}
                          alt="Assembled Puzzle"
                          style={{ width: "100%", maxHeight: "400px" }}
                        />

                     )}
                    {pieces.map((piece, index) => (
                    <div
                      key={index}
                      onMouseDown={(event) => handleMouseDown(event, index)}
                      onMouseUp={handleMouseUp}
                      style={{
                        backgroundImage: `url(${piece.src})`,
                        backgroundSize: "cover",
                        width: "100px",
                        height: "100px",
                        position: "absolute",
                        left: `${piece.position[0]}px`,
                        top: `${piece.position[1]}px`,
                      }}
                    >
                      {index}
                    </div>
                  ))}
                </div>
              </div>
            <div className="d-flex justify-content-between">
                <VerifyPuzzle data={assembledPuzzles}/>
                <button
                  style={{
                    padding: "1rem",
                    color: "#fff",
                    backgroundColor: 'black',
                  }}
                  onClick={handleAutoCompletePuzzles}
                >
                  Automatically Complete Puzzles
                </button>
              </div>
              <p className="lead mb-4">
                  When checking the correctness of the assembled sequence of puzzles,
                  the button will light up in red if the sequence is assembled incorrectly
                  and green in the case of a correctly assembled picture.
              </p>
            </div>
          </div>
        </main>
        <Footer />
      </div>
    );
    }

export default ManualAssemblyPage;