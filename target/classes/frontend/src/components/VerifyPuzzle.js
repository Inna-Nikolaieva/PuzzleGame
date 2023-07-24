import React, {useEffect, useRef, useState} from 'react';
import "../api/services";
import {splitImage, verifyPuzzles} from "../api/services";


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
        let base64Images = data.map(d => d.image.src.split(',')[1]);
        const response = await verifyPuzzles(base64Images);
        setIsSuccess(response);
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