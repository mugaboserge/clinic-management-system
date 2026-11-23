import { useState } from "react";
import axios from "axios";

function Office() {

    const [officeName, setOfficeName] = useState("");
    const [officeNumber, setOfficeNumber] = useState("");

    async function CreateAccount(e) {
        e.preventDefault();

        try {
            const response = await axios.post(
                "http://localhost:1014/api/offices",
                {
                    name: officeName,
                    officeNumber: officeNumber
                }
            );

            alert("office created bro!");
            console.log(response.data);

        } catch (error) {
            alert("office nago ikunze ongera ugerageze");
            console.log(error);
        }
    }

    return (
        <div>
            <h1>SAVE OFFICE</h1>

            <form onSubmit={CreateAccount}>

                <label>Office Name</label>
                <input
                    type="text"
                    placeholder="Enter office name"
                    value={officeName}
                    onChange={(e) => setOfficeName(e.target.value)}
                    required
                />

                <label>Office Number</label>
                <input
                    type="text"
                    placeholder="Enter office number"
                    value={officeNumber}
                    onChange={(e) => setOfficeNumber(e.target.value)}
                    required
                />

                <button type="submit">save office</button>

            </form>
        </div>
    );
}

export default Office;
