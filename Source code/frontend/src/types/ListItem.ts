export interface ListItem {
  id: string;
  name: string;
  time: number;
}

export interface ListData {
  items: ListItem[];
  totalTime: number;
  name: string;
}
