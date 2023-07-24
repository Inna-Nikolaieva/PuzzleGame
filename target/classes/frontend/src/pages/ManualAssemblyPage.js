import React, { useState, useMemo, useRef } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import PuzzleUploader from "../components/PuzzleUploader";
import VerifyPuzzle from "../components/VerifyPuzzle";

const ManualAssemblyPage = () => {

     const [assembledPuzzles, setAssembledPuzzles] = useState([]);
     const [selectedImages, setSelectedImages] = useState([]);
     const [puzzleData, setPuzzleData] = useState([]);

     const handleImageUpload = (images) => {
       setSelectedImages(images);

       // Process the images to create the puzzleData
           const newPuzzleData = images.map((image, index) => ({
             src: URL.createObjectURL(image),
             name: `Piece ${index + 1}`,
             position: [Math.random() * 300, Math.random() * 300 + 300],
           }));

           setPuzzleData(newPuzzleData);
     };


     const pieces = useMemo(() => {
        // Use the puzzleData state to generate the pieces data
        return puzzleData;
    }, [puzzleData]);

      const selected = useRef();

      const handleMouseDown = (event, index) => {
        const { offsetX, offsetY } = event.nativeEvent;
        selected.current = { index, offsetX, offsetY };
        document.addEventListener("mousemove", handleMouseMove);
      };

      const handleMouseMove = (event) => {
        const { index, offsetX, offsetY } = selected.current;
        const positionX = event.pageX - offsetX;
        const positionY = event.pageY - offsetY;
        pieces[index].position = [positionX, positionY];
      };

      const handleMouseUp = () => {
        const sortedPieces = sortPiecesByPosition(pieces);
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

          // Compare the y position first
          if (Math.abs(ay - by) > 5) {
            return ay - by;
          }

          // If y positions are within the range, compare the x position
          if (Math.abs(ax - bx) > 5) {
            return ax - bx;
          }

          // If both x and y positions are within the range, consider them equal
          return 0;
        });
      };

      return (
        <div>
          <Header />
          <main>
            {/* Use the PuzzleUploader component */}
            <PuzzleUploader onImageUpload={handleImageUpload} />
            <div>
              {pieces.map((piece, index) => (
                <div
                  key={index}
                  onMouseDown={(event) => handleMouseDown(event, index)}
                  onMouseUp={handleMouseUp}
                  style={{
                    backgroundImage: `url(${piece.src})`, // Use piece.src instead of piece.image.src
                    backgroundSize: "cover",
                    width: "100px",
                    height: "100px",
                    position: "absolute",
                    left: `${piece.position[0]}px`,
                    top: `${piece.position[1]}px`
                  }}
                >
                  {index}
                </div>
              ))}
            </div>
            <VerifyPuzzle data={assembledPuzzles}/>
        </main>
          <Footer />
        </div>
      );
    };

//     return (
//       <div>
//         <Header />
//         <main className="container mt-5">
//           <PuzzleUploader onImageUpload={handleImageUpload} />
//           <div className="row">
//             <div className="col-md-6">
//               {/* Area to display loaded puzzles */}
//               <div className="card mb-3">
//                 <div className="card-header">Assemble Puzzles</div>
//                 <div className="card-body" style={{ minHeight: "400px" }}>
//                   {pieces.map((piece, index) => (
//                     <div
//                       key={index}
//                       onMouseDown={(event) => handleMouseDown(event, index)}
//                       onMouseUp={handleMouseUp}
//                       style={{
//                         backgroundImage: `url(${piece.src})`,
//                         backgroundSize: "cover",
//                         width: "100px",
//                         height: "100px",
//                         position: "absolute",
//                         left: `${piece.position[0]}px`,
//                         top: `${piece.position[1]}px`,
//                       }}
//                     >
//                       {index}
//                     </div>
//                   ))}
//                 </div>
//                 <button className="btn btn-primary" onClick={handleVerifyPuzzle}>
//                   Verify Puzzle Assembly
//                 </button>
//               </div>
//             </div>
//           </div>
//         </main>
//         <Footer />
//       </div>
//     );
//   };


export default ManualAssemblyPage;
