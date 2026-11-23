
import { useState } from "react";
import LibraryButton from "./LibraryButton";
import "./LoginPage.css";

function LoginPage() {
    const [userName, setUserName] = useState("");
    const [password, setPassword] = useState("");
    const [studentId, setStudentId] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [doYouHaveAccount, setDoYouHaveAccount] = useState(true);

    function checkLogin() {
        alert("Welcome " + userName);
    }

    function changeContent() {
        setDoYouHaveAccount(!doYouHaveAccount);
    }

    return (
        <div className="login-container">
            <div className="login-card">

                <h1>MyLibrary</h1>

                {doYouHaveAccount ? (
                    <>
                        <h2>Sign In</h2>
                        <p>Access your academic portal</p>

                        <form>
                            <label>Username:</label>
                            <input type="text" placeholder="Enter username" onChange={(e) => setUserName(e.target.value)} />

                            <label>Password:</label>
                            <input type="password" placeholder="Enter password" onChange={(e) => setPassword(e.target.value)} />

                            <LibraryButton onClick={checkLogin}>LOGIN</LibraryButton>
                        </form>

                        <p>New here?</p>
                        <LibraryButton onClick={changeContent}>Create Account</LibraryButton>
                    </>
                ) : (
                    <>
                        <h2>Create Account</h2>
                        <p>Access your academic portal</p>

                        <form>
                            <label>Username:</label>
                            <input type="text" placeholder="Enter username" onChange={(e) => setUserName(e.target.value)} />

                            <label>Student ID:</label>
                            <input type="text" placeholder="Enter student ID" onChange={(e) => setStudentId(e.target.value)} />

                            <label>Password:</label>
                            <input type="password" placeholder="Enter password" onChange={(e) => setPassword(e.target.value)} />

                            <label>Confirm Password:</label>
                            <input type="password" placeholder="Confirm password" onChange={(e) => setConfirmPassword(e.target.value)} />

                            <LibraryButton>CREATE ACCOUNT</LibraryButton>
                        </form>

                        <p>Already have an account?</p>
                        <LibraryButton onClick={changeContent}>Sign In</LibraryButton>
                    </>
                )}

            </div>
        </div>
    );
}

export default LoginPage;
