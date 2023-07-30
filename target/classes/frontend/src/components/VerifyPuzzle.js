import React, {useEffect, useRef, useState} from 'react';
import axios from "axios";

const VerifyPuzzle = (props) => {
    const {data} = props;
    const [isSuccess, setIsSuccess] = useState(null);
    let buttonColor;

    if (isSuccess === null) {
        buttonColor = 'black';
    } else if (!isSuccess) {
        buttonColor = 'red';
    } else {
        buttonColor = 'green';
    }

    async function handleAssembledPuzzles() {
        try {
              let base64Images = data.map((d) => d.image.src.split(',')[1]);

              const response = await axios.post('http://localhost:8081/api/verifyPuzzles', base64Images);

              setIsSuccess(response.data);
            } catch (error) {
              console.error('Error verifying puzzles:', error);
              setIsSuccess(false);
            }
    }

    return (
        <div>
            <button
                style={{padding: "1rem",
                    color: "#fff",
                    backgroundColor: buttonColor,
                }}

                onClick={handleAssembledPuzzles}
            >
                Verify Puzzles
            </button>
        </div>
    );
};

export default VerifyPuzzle;