import type { Base } from "./Base";
import type { Project } from "./Project";
import type { User } from "./User";

export interface Role extends Base {
  projectName?: string;
  name: string;
  isPrivileged: boolean;
  project: Project;
  users?: User[];
}
