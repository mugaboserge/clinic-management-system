import { useState, useEffect } from "react";
import axios from "axios";

function Offices() {
    const [offices, setOffices] = useState([]);
    const [search, setSearch] = useState("");

    // CONNECT TO OUR DB CLINIC-DB

    const fetchOffices = () => {
        axios.get("http://localhost:8082/api/offices")
            .then(response => {
                setOffices(response.data);
            })
            .catch(error => {
                console.log("Error fetching offices:", error);
            });
    };

    useEffect(() => {
        fetchOffices();
    }, []);

 // SEARCH
    const filteredOffices = offices.filter(office =>
        office.name.toLowerCase().includes(search.toLowerCase()) ||
        office.officeNumber.toString().includes(search)
    );

// DELETE
    const deleteOffice = (id) => {
        if (window.confirm("Are you sure you want to delete this office?")) {
            axios.delete(`http://localhost:8082/api/offices/${id}`)
                .then(() => {
                    alert("Office deleted successfully!");
                    fetchOffices();
                })
                .catch(error => {
                    console.log("Error deleting office:", error);
                });
        }
    };

 // UPDATE
    const updateOffice = (office) => {
        const newName = prompt("Enter new office name:", office.name);
        const newOfficeNumber = prompt(
            "Enter new office number:",
            office.officeNumber
        );

        if (newName !== null && newOfficeNumber !== null) {
            const updatedOffice = {
                name: newName,
                officeNumber: newOfficeNumber
            };

            axios.put(
                `http://localhost:8082/api/offices/${office.id}`,
                updatedOffice
            )
                .then(() => {
                    alert("Office updated successfully!");
                    fetchOffices();
                })
                .catch(error => {
                    console.log("Error updating office:", error);
                });
        }
    };

    return (
        <div style={{ padding: "20px" }}>

            <h1>Available Offices</h1>

            {/* Search Section */}
            <div style={{ marginBottom: "20px" }}>
                <input
                    type="text"
                    placeholder="Search office name or number..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    style={{
                        padding: "10px",
                        width: "250px"
                    }}
                />

                <button
                    onClick={() => setSearch(search)}
                    style={{
                        padding: "10px",
                        marginLeft: "10px"
                    }}
                >
                    Search
                </button>

                <button
                    onClick={() => setSearch("")}
                    style={{
                        padding: "10px",
                        marginLeft: "10px"
                    }}
                >
                    Clear
                </button>
            </div>

            {/* Offices Table */}
            <table border="1" cellPadding="10" width="100%">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Office Name</th>
                    <th>Office Number</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                {filteredOffices.map(office => (
                    <tr key={office.id}>
                        <td>{office.id}</td>
                        <td>{office.name}</td>
                        <td>{office.officeNumber}</td>

                        <td>
                            <button
                                onClick={() => updateOffice(office)}
                            >
                                Update
                            </button>

                            <button
                                onClick={() => deleteOffice(office.id)}
                                style={{ marginLeft: "10px" }}
                            >
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}

                {filteredOffices.length === 0 && (
                    <tr>
                        <td colSpan="4">
                            No offices found.
                        </td>
                    </tr>
                )}
                </tbody>
            </table>

        </div>
    );
}

export default Offices;