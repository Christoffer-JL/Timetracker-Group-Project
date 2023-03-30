import { apiUrl } from "./apiConfig";
const url = `${apiUrl}`;


export const login = async () => {
  let res = await newPost("/auth/login", {});
};

export const logout = async () => {
  let res = await newPost("/auth/logout", {});
};

const newPost = async (path: string, body: object) => {
  return await fetch(
    new Request(url + path, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
      credentials: "include",
    })
  );
};

const newGet = async (path: string) => {
  return await fetch(
    new Request(url + path, { method: "GET", credentials: "include" })
  );
};

const newPut = async (path: string, body: object) => {
  return await fetch(
    new Request(url + path, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
      credentials: "include",
    })
  );
};

const newDelete = async (path: string, body: object) => {
  return await fetch(
    new Request(url + path, {
      method: "DELETE",
      body: JSON.stringify(body),
      credentials: "include",
    })
  );
};

// Users

export const getUser = async (id: string) => {
  let res = await newGet("/users/" + id);

  return await res.json();
};

export const getAllUsers = async () => {
  let res = await newGet("/users");

  return await res.json();
};

export const createUser = async (body: object) => {
  let res = await newPost("/users", body);

  return await res.json();
};

export const updateUser = async (id: string, body: object) => {
  let res = await newPut("/users/" + id, body);

  return await res.json();
};

export const deleteUser = async (id: string) => {
  let res = await newDelete("/users/" + id, {});

  return await res.json();
};

export const resetPassword = async (id: string) => {
  let res = await newPut("/users/" + id, {});

  return await res.json();
};

// Clockins

export const getClockin = async (id: string) => {
  let res = await newGet("/clockins/" + id);

  return await res.json();
};

export const getAllClockins = async () => {
  let res = await newGet("/clockins");

  return await res.json();
};

export const createClockin = async (body: object) => {
  let res = await newPost("/clockins", body);

  return await res.json();
};

export const updateClockin = async (id: string, body: object) => {
  let res = await newPut("/clockins/" + id, body);

  return await res.json();
};

export const deleteClockin = async (id: string) => {
  let res = await newDelete("/clockins/" + id, {});

  return await res.json();
};

export const sign = async (id: string) => {
  // id here?
  let res = await newPut("/sign/" + id, {});

  return await res.json();
};

// Roles

export const getRole = async (id: string) => {
  let res = await newGet("/roles/" + id);

  return await res.json();
};

export const getAllRoles = async () => {
  let res = await newGet("/roles");

  return await res.json();
};

export const createRole = async (body: object) => {
  let res = await newPost("/roles", body);

  return await res.json();
};

export const updateRole = async (id: string, body: object) => {
  let res = await newPut("/roles/" + id, body);

  return await res.json();
};

export const deleteRole = async (id: string) => {
  let res = await newDelete("/roles/" + id, {});

  return await res.json();
};

// Activities

export const getActivity = async (id: string) => {
  let res = await newGet("/activities/" + id);

  return await res.json();
};

export const getAllActivities = async () => {
  let res = await newGet("/activities");

  return await res.json();
};

export const createActivity = async (body: object) => {
  let res = await newPost("/activities", body);

  return await res.json();
};

export const updateActivity = async (id: string, body: object) => {
  let res = await newPut("/activities/" + id, body);

  return await res.json();
};

export const deleteActivity = async (id: string) => {
  let res = await newDelete("/activities/" + id, {});

  return await res.json();
};

// Projects

export const getProject = async (id: string) => {
  let res = await newGet("/projects/" + id);

  return await res.json();
};

export const getAllProjects = async () => {
  let res = await newGet("/projects");

  return await res.json();
};

export const createProject = async (body: object) => {
  let res = await newPost("/projects", body);

  return await res.json();
};

export const updateProject = async (id: string, body: object) => {
  let res = await newPut("/projects/" + id, body);

  return await res.json();
};

export const deleteProject = async (id: string) => {
  let res = await newDelete("/projects/" + id, {});

  return await res.json();
};
